const infinispan = require("infinispan");

async function delay(ms) {
    // return await for better async stack trace support in case of errors.
    return await new Promise(resolve => setTimeout(resolve, ms));
}

async function test() {
    await new Promise((resolve, reject) => setTimeout(() => resolve(), 1000));
    console.log('Hello, World!');

    try {
        let client = await infinispan.client({port: 11222, host: '127.0.0.1'});
        console.log(`Connected to Infinispan dashboard data`);

        await outer(client);
    } catch (error) {
        console.log("Got error: " + error.message);
        console.log("Got error: " + error);
        await test();
    }

}

async function outer(client) {
    await client.clear();

    await innerLoop(client);

    await client.disconnect();
}

async function innerLoop(client) {
    for (var i = 0; i < 100000; i++) {
        await delay(1000);

        let key = 'k' + i;
        let value = 'v' + i;
        await client.put(key, value);

        let v = await client.get(key);
        console.log('get(' + key + '): ' + v);
    }
}

test();
