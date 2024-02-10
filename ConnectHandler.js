const AWS = require('aws-sdk');
const ddb = new AWS.DynamoDB.DocumentClient();
exports.handler = async function (event, context) {
try {
    await ddb
    .put({
        TableName: process.env.session_table,
        Item: {
        connectionId: event.requestContext.connectionId,
        userId: event.headers['user-id'],
        },
    })
    .promise();
} catch (err) {
    return {
    statusCode: 500,
    };
}
return {
    statusCode: 200,
};
};