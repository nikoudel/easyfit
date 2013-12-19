![easyfit](https://raw.github.com/nikoudel/easyfit/images/logo.png)

Easyfit is a [FitNesse](http://fitnesse.org) plugin. Its main goal is to facilitate connection of automated tests to an existing system under test (SUT). Easyfit tries to achieve this goal with a minimalistic yet flexible approach:

1. reduce [boilerplate code](http://en.wikipedia.org/wiki/Boilerplate_code) to minimum by utilizing external libraries for data serialization
2. provide only two tables to access SUT making easyfit really easy to learn
3. make these tables flexible enough to get the job done
4. use a standard communication protocol between FitNesse and SUT

Easyfit utilizes RESTful frameworks and http(s) protocol for SUT communication making it suitable for testing any kind of application platforms with decent REST support (eg. [ServiceStack](https://servicestack.net) for .NET and [Restlet](http://restlet.org) for Java). [RESTful approach](http://en.wikipedia.org/wiki/Representational_state_transfer) not only automates the boring and error-prone task of test data deserialization and routing (the boilerplate code mentioned above) but also should lead to a cleaner application API and overall architecture, especially with [TDD](http://en.wikipedia.org/wiki/Test-driven_development) process.

Easyfit has only two tables handling all SUT communication (Query and Row). These two tables are enough to interact with a system of any complexity because they incapsulate the [CRUD](http://en.wikipedia.org/wiki/Create,_read,_update_and_delete) operations.

Query table represents the Read operation. It gets a list of search arguments and passes them to SUT which, in turn, returns a list of items (rows), just like the Slim [Query Table](http://fitnesse.org/FitNesse.UserGuide.SliM.QueryTable).

Row table corresponds to Slim [Decision Table](http://fitnesse.org/FitNesse.UserGuide.SliM.DecisionTable); it calls SUT row by row and represents the rest three CRUD operations: Create, Update and Delete.

So, how these tables differ from FitNesse Slim tables? Essentially, they don't. But they are the only two necessary tables to utilize RESTful services which is the inherent part of this project. The following sections will describe easyfit in details.

TODO...