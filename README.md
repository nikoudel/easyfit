![easyfit](https://raw.github.com/nikoudel/easyfit/images/logo.png)

Easyfit is a [FitNesse](http://fitnesse.org) plugin. Its main goal is to facilitate connection of automated tests to an existing system under test (SUT). Easyfit tries to achieve this goal with a minimalistic yet flexible approach:

1. reduce [boilerplate code](http://en.wikipedia.org/wiki/Boilerplate_code) to minimum by utilizing external libraries for data serialization
2. provide only two tables to access SUT making Easyfit really easy to learn
3. make these tables flexible enough to get the job done in a convenient way
4. use a standard communication protocol between FitNesse and SUT

Easyfit takes advantage of RESTful frameworks in several ways. First, it utilizes http(s) protocol for SUT communication making it suitable for testing any kind of applications built on platforms with decent REST support (eg. [ServiceStack](https://servicestack.net) for .NET and [Restlet](http://restlet.org) for Java). Second, the frameworks reduce maintenance effort by handling the boring and error-prone task of test data (de)serialization and routing (the boilerplate code mentioned above). Finally, although Easyfit doesn't force the user to apply the classical [RESTful approach](http://en.wikipedia.org/wiki/Representational_state_transfer), it makes it easy to adopt one, possibly leading to a cleaner application API and overall architecture, especially with [TDD](http://en.wikipedia.org/wiki/Test-driven_development) process.

Easyfit has only two tables handling all SUT communication (Query and Row). These two tables are enough to interact with a system of any complexity because they incapsulate the [CRUD](http://en.wikipedia.org/wiki/Create,_read,_update_and_delete) operations. 

Query table is comparable to SQL *select* operation in terms of _structure_ of the data being exchanged. It gets a list of search arguments and passes them to SUT which, in turn, returns zero or more items (rows), just like the Slim [Query Table](http://fitnesse.org/FitNesse.UserGuide.SliM.QueryTable). *However*, it shouldn't be considered only a Read operation – it's called "query" only because the amount of rows to be returned is uncertain. Query table can still be used, for example, for deleting a bunch of rows based on certain criteria and returning a list of ID's of the deleted rows.

Row table corresponds to Slim [Decision Table](http://fitnesse.org/FitNesse.UserGuide.SliM.DecisionTable); it calls SUT row by row so that, on each call,  there is exactly one row going into the SUT and exactly one row coming out of it.

So, how these tables differ from FitNesse Slim tables? Essentially, they don't. But they are the only two tables necessary to utilize the http GET and POST methods which is the inherent part of this project. They also have some additional features such as symbol (variable) initialization inside the Query table and input/output value filters. The details are going to be described next.

TODO...
