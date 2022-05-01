## Introduction 

This work is in response to an assignment given to me by SailPoint. 

## Requirements

To run the application, the machine you are using needs to have the following

* docker
* docker-compose
* curl

Please ensure that the docker engine is running and the machine can be connected to the Internet. 

## Setup the application

This `README` assumes the working directory to be the `PROJECT_ROOT`. If you are not already in the project root, please `cd` into it. 

```shell
cd ${PROJECT_ROOT}
```

#### Prepare initialization data

* Create a folder named `init-db` in the `${PROJECT_ROOT}/infra` directory.
```shell
mkdir -p infra/init-db
```
* Copy the initialization data in the file `shared-local-instance.db` into this `init-db` folder. 
```shell
cp infra/shared-local-instance.db infra/init-db
```

#### Prepare the application's docker image
* You can use gradle to do this
```shell
./gradlew bootBuildImage
```

#### Start the application

We will use `docker-compose` to do this. Run the following command and the application will start up

```shell
docker-compose up
```

Doing this will spin up 2 docker containers, one named `dynamodb-local` which is the database I am using and the other is `application` which is the ATM service that I as asked to build. This command will run in the foreground and application logs can be seen in this tab of the terminal. 

Please verify that both services are up and running. We can use telnet to check if the ports that the services use are listening. 

```shell
telnet localhost 8080
telnet localhost 8000
```

The ATM service listens on port `8080` and dynamodb listens on port `8000`. 

When the application starts up, the initialization that we prepared in an earlier stage will also be loaded into DynamoDB. 

## Run the application

To run the application, you need to switch to a new terminal window (or open a new tab in the existing terminal window). From this terminal window, we use curl to hit the endpoints available.

All endpoints return `Http 2xx` status codes if the request was processed successfully. Unfortunately, at the moment, I am not returning meaningful error messages back. So if there is an error, the curl command gets a `Http 4xx` or `Http 5xx` status code. To see why this status was returned, you can check the application logs in the other terminal window. 

#### Login api
This api listens on the endpoint `/login`. The following data is already available to test this endpoint. 
```json
[
  {"customerID": "jane_5678", "pin": "2989"},
  {"customerID":  "john_123", "pin": "1234"}
]
```
You can run the following `curl` command to call this api
```shell
curl -i -X POST http://localhost:8080/login -H "Content-Type: application/json" -d '{"customerID": "john_123", "pin": "1234"}'
```

Using anything other than the data provided will lead to a `Http 4xx` error being returned. 

#### Get Account Details api

This is a `GET` api and listens to the endpoint `/account/{account_number}`. The following account numbers can be used to test this endpoint. 
```text
5DBE0987-D684-4723-828A-9374A5F42F4B
C25D1498-9531-4D18-B10C-75E462E05F98
AC8A297F-8644-4EEC-9857-1BD5E74AF264
74429352-118B-472F-A0EF-CDFBCB8837D9
3BA63DF4-E534-4410-9008-3B27003240F7
```

The following curl command can be used to test this api.

```shell
curl -i http://localhost/account/3BA63DF4-E534-4410-9008-3B27003240F7
```

If an account number other than the ones provided is used, you will get a `Http 404` indicating that Account with the number provided was not found in the system. 

#### Deposit Money api

This is a `PATCH` type API and listens to the endpoint `/account/{account_number}/deposit`. The same initialization provided for the `Get Account Details` api can be used here as well. 

The following `curl` command can be used to test the api

```shell
curl -i -X PATCH http://localhost:8080/account/C25D1498-9531-4D18-B10C-75E462E05F98/deposit -H "Content-Type: application/json" -d '{"amount": 100}'
```

If an account number that is not already available is used, a `Http 404` is returned. If the amount to deposit is less than 0, then a `Http 400` is returned. 

#### Withdraw Money api 

This is a `PATCH` type API and listens to the endpoint `/account/{account_number}/withdraw`. The same initialization data provided for the `Get Account Details` api can be used here as well. 

The following `curl` command can be used to test the api

```shell
curl -i -X PATCH http://localhost:8080/account/C25D1498-9531-4D18-B10C-75E462E05F98/withdraw -H "Content-Type: application/json" -d '{"amount": 100}'
```

If an account number that is not present in the database is used, a `Http 404` is returned. If the amount to withdraw is negative or more than the balance available in the account, a `Http 400` is returned. 

## Improvements

This is by no means a complete solution and many more improvements can be done to this system. Some that come to me are

* Use the cloud version of DynamoDB instead of the local setup
* Do not store credentials as plaintext in the database. Right now, the `pin`, that is a part of the customer's credentials are stored in plaintext. 
* Do not pass aws `access_key` and `secret` in plaintext through configurations. If we use AWS EKS to deploy, we should use the k8s service accounts and IRSA (IAM Roles for Service Accounts) roles to get access to AWS resources. 
* Use a `write-through` cache instead of hitting the database all the time. I feel that this service would get far more read requests than write requests. In such a case, having this cache can speed up requests. 
* Create an open api spec for the APIs before implementing the apis. Jumping into the implementation before having the design ready is usually not a good idea. 
* Make a record of transactions, especially the deposit and withdraw transactions. This can be done in another table in the database and can be done asynchronously. This can be used by customers to see the transactions they made and also as an audit trail. 
* Return more meaningful error messages. Right now, if there is an error, only a `Http 4xx` or `Http 5xx` status code is returned. As an improvement, implementing `RFC 7807` would make the APIs more user-friendly. 