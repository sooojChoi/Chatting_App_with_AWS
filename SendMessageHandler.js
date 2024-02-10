const AWS = require('aws-sdk');
      const ddb = new AWS.DynamoDB.DocumentClient();
      
      exports.handler = async function (event, context) {
        let connections;
        try {
          // Retrieve all connection IDs from DynamoDB
          connections = await ddb.scan({ TableName: process.env.table }).promise();
        } catch (err) {
          return {
            statusCode: 500,
          };
        }
        
        const callbackAPI = new AWS.ApiGatewayManagementApi({
          apiVersion: '2018-11-29',
          endpoint:
            event.requestContext.domainName + '/' + event.requestContext.stage,
        });
      
       // Parse the message from the incoming WebSocket event
        const message = JSON.parse(event.body);
        
  
        // messgae와 room 항목을 table에 추가, 업데이트 한다. 
        try{
          await ddb
            .put({
              TableName: process.env.message_table,
              Item: {
                id: message.msgId,
                from_id: message.fromId,
                text: message.text,
                datetime: message.msgDateTime,
                room_id: message.msgRoomId,
                type: message.type,
                from_name: message.fromName,
                __typename: "Message",
                _lastChangedAt: 1705132376447,
                _version: 1,
                createdAt: "2024-01-13T07:52:56.421Z",
                updatedAt: "2024-01-13T07:52:56.421Z",
              },
            })
            .promise();
            
          await ddb.put(
                {
                TableName: process.env.room_table,
                Item: {
                  __typename: "Room",
                   _lastChangedAt: 1705132376447,
                   _version: 1,
                   createdAt: "2024-01-13T07:52:56.421Z",
                   updatedAt: "2024-01-13T07:52:56.421Z",
                  last_msg_sender: message.fromName,
                  name: message.roomName,
                  last_msg_time: message.lastMsgTime,
                  last_msg: message.lastMsg,
                  id: message.roomId,
                  members: message.members
                },
              })
              .promise();
        } catch(err){
          return {
            statusCode: 500,
          };
        }
        
        const sendingMessage = JSON.stringify(JSON.parse(event.body));
        const members = message.members.split("\n");
        const sendMessages = members.map( async (element)=>{
          try{
            await Promise.all(connections.Items.map(async ({ connectionId, userId }) => {
              if(userId==element){
                await callbackAPI
                  .postToConnection({ ConnectionId: connectionId, Data: sendingMessage })
                  .promise();  
              }
            })
            );
          }catch (e) {
            console.log(e);
            return {
              statusCode: 500,
            };
          }
          
        });
    
      
        try {
          await Promise.all(sendMessages);
        } catch (e) {
          console.log(e);
          return {
            statusCode: 500,
          };
        }
      
      
      
      
        return { statusCode: 200 };
      };