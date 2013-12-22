![easyfit](https://raw.github.com/nikoudel/easyfit/images/logo.png)

Easyfit is a [FitNesse](http://fitnesse.org) plugin. Its main goal is to lower the threshold of creating and attaching FitNesse tests to an existing system under test (SUT). Easyfit was built with the following objectives in mind:

1. reduce boilerplate code to minimum by utilizing external libraries for data serialization
2. provide only two tables to access SUT in order to make learning easier and misusing harder
3. make these tables flexible enough to get the job done in a convenient way
4. use a standard communication protocol between FitNesse and SUT

Easyfit takes advantage of RESTful frameworks in several ways. First, it utilizes http(s) protocol for SUT communication making it suitable for testing any kind of applications built on platforms with decent REST support (eg. [ServiceStack][ss] for .NET and [Restlet](http://restlet.org) for Java). Second, the frameworks reduce maintenance effort by handling the boring and error-prone task of test data (de)serialization and routing (the boilerplate code mentioned above). Finally, although Easyfit doesn't force the user to apply the classical [RESTful approach](http://en.wikipedia.org/wiki/Representational_state_transfer), it makes it easy to adopt one, possibly leading to a cleaner application API and overall architecture, especially with [TDD](http://en.wikipedia.org/wiki/Test-driven_development) process.

Easyfit has only two tables handling all SUT communication (Query and Row). These two tables incapsulate the [CRUD](http://en.wikipedia.org/wiki/Create,_read,_update_and_delete) operations enabling interactions with systems of any complexity.

Query table is comparable to SQL *select* operation in terms of *structure* of the data being exchanged. It gets a list of search arguments and passes them to SUT which, in turn, returns zero or more items (rows), just like the Slim [Query Table](http://fitnesse.org/FitNesse.UserGuide.SliM.QueryTable). *However*, it shouldn't be considered only a Read operation – it's called "query" only because the amount of rows to be returned is uncertain. Query table can still be used, for example, for deleting a bunch of items based on certain criteria and returning a list of ID's of the deleted items.

Row table corresponds to Slim [Decision Table](http://fitnesse.org/FitNesse.UserGuide.SliM.DecisionTable); it calls SUT row by row so that, on each call, there is exactly one row (item) going into the SUT and exactly one row coming out of it.

So, how these tables differ from FitNesse Slim tables? Essentially, they don't. But they are the only two tables necessary to utilize the http GET and POST methods which is the inherent part of this project. They also have some additional features such as symbol (variable) initialization inside the Query table and input/output value filters. The details will be described next.

Getting started
===============

The easiest way to try easyfit is to [download](https://github.com/nikoudel/SmartHome/archive/master.zip) or clone the [SmartHome](https://github.com/nikoudel/SmartHome) demo project:

    git clone https://github.com/nikoudel/SmartHome.git

The project is a .NET console application demonstrating all easyfit features. SmartHome can be built from source but it's not required because all the needed binaries (including FitNesse) are available.

The *bin* directory in the project root contains SmartHomeTests.exe which provides the testing interface of the SUT. When executed, it starts listening for http connections from FitNesse. The *FitNesse* directory (also in the root) contains FitNesse, easyfit and all their dependencies. So, to run some tests

1. [download](https://github.com/nikoudel/SmartHome/archive/master.zip) and extract the SmartHome project somewhere
2. run it by executing SmartHome\bin\SmartHomeTests.exe
3. start fitnesse by running SmartHome\FitNesse\start.bat
4. point your browser to the [TurnKitchenLightsOnOff][lights] test and press the Test button

**Note:** step 3 requires [Java JRE](http://www.oracle.com/technetwork/java/javase/downloads/index.html) to be installed and java.exe to be added to PATH environment variable. Alternatively you can modify start.bat to point to java.exe with absolute path, for example like this:

    "C:\Program Files\Java\jre7\bin\java.exe" -jar fitnesse-standalone.jar -p 8080

The test mentioned above should pass. It tests an imaginary [home automation](http://en.wikipedia.org/wiki/Home_automation) system which lets the user operate and monitor house devices (eg. lights, coffee maker, dish washer etc.) over network with, let's say, a smart phone. In particular, [TurnKitchenLightsOnOff][lights] tests that kitchen lights turn on and off as expected. However, before the test gets to a point where it can turn the lights on and off, a lot of things needs to happen, so let's start from the beginning.

After having started FitNesse point your browser to

    http://localhost:8080/SmartHome


![](https://raw.github.com/nikoudel/easyfit/images/SmartHome.PNG "SmartHome main page")

This is the root page of SmartHome test suite in FitNesse. It has two tests below it (SaunaSchedule and TurnKitchenLightsOnOff), the SuiteSetUp page and a couple of configuration lines.

The TEST_SYSTEM variable defines [Slim](http://fitnesse.org/FitNesse.UserGuide.SliM) test system to be enabled everywhere below the [SmartHome](http://localhost:8080/SmartHome) page. Slim test system gives access to the [Table](http://fitnesse.org/FitNesse.UserGuide.SliM.TableTable) FitNesse table which is the basement easyfit is built on top of.

The classpath definitions add easyfit dependencies:

<dl>
	<dt>.\lib</dt>
	<dd>adds the lib directory to the classpath (needed for accessing the logging configuration file log4j2.xml)</dd>
	<dt>.\lib\scala-library.jar</dt>
	<dd>adds the <a href="http://www.scala-lang.org">Scala</a> library (easyfit is written in Scala)</dd>
	<dt>.\lib\easyfit_2.10-0.1.jar</dt>
	<dd>adds the easyfit library itself</dd>
	<dt>.\lib\log4j-api-2.0-beta9.jar</dt>
	<dd>part of log4j logging utility</dd>
	<dt>.\lib\log4j-core-2.0-beta9.jar</dt>
	<dd>part of log4j logging utility</dd>
	<dt>.\lib\filters_2.10-1.0.jar</dt>
	<dd>optional filtering library (will be explained later on this page)</dd>
</dl>

SuiteSetUp is a [set-up](http://fitnesse.org/FitNesse.UserGuide.TestSuites.SuiteSetUpAndSuiteTearDown) page which prepares the initial state for all tests in a suite. It gets executed automatically upon running a test (before the test). When running the whole suite (a collection of tests), SuiteSetUp gets executed only once.

In this case [SuiteSetUp](http://localhost:8080/SmartHome.SuiteSetUp) initialized the SUT by creating domain objects, namely rooms and devices:

![](https://raw.github.com/nikoudel/easyfit/images/SuiteSetup.PNG ".SmartHome.SuiteSetUp")

First, the [easyfit.tables](https://github.com/nikoudel/easyfit/tree/master/src/main/scala/easyfit/tables) namespace is [imported](http://fitnesse.org/FitNesse.UserGuide.SliM.ImportTable). Now the tables can be access with a shorter name (Table:Row instead of Table:easyfit.tables.Row).

Next, filter "tf" is defined. Filters help to overcome possible JSON serialization issues. Filters will be explained later on this page.

Finally, the SUT call: SuiteSetUp includes three Row tables (CreateOtherDevices is similar to the other two and is collapsed to save screen space).

Row table
---------

Every table has a name (Row in this case). Row and Query tables have SUT action (CreateRoom) and a header row. Row table has at least one row. In this case it has three rows, so the SUT will be called three times.

![](https://raw.github.com/nikoudel/easyfit/images/RowTable.png "Row table")

Table name tells FitNesse which table to create. Easyfit Row table is in [Row.scala](https://github.com/nikoudel/easyfit/blob/master/src/main/scala/easyfit/tables/Row.scala) file.

SUT action tells the table which operation to call on the SUT side. The following code sample demonstrates easyfit's entry point into the SUT which is implemented using the [ServiceStack][ss] communication framework (an alternative to Microsoft WCF).

CreateRoom action leads to CreateRoomService.Post method in [CreateRoomService.cs](https://github.com/nikoudel/SmartHome/blob/master/SmartHomeTests/Services/CreateRoomService.cs):

```csharp
/// <summary>
/// An http POST to /CreateRoom URL will be serialized as RoomData and passed to
/// CreateRoomService.Post method which returns a RoomData object to be serialized
/// and sent back to the caller over http.
/// </summary>
[Route("/CreateRoom", "POST")]
public class RoomData
{
    public long Id { get; set; }
    public RoomType RoomType { get; set; }
}

public class CreateRoomService : Service
{
    //Injected by IOC (see ApplictionHost.Configure)
    public Repository<Room> Repository { get; set; }

    /// <summary>
    /// Create and add a new Room into the repository.
    /// </summary>
    public RoomData Post(RoomData data)
    {
        // Note: repository creates a Room, not RoomData.
        var room = Repository.Store(Room.createInstance(data.RoomType));

        // set room ID back to the data object
        data.Id = room.Id;

        return data;
    }
}
```

The CreateRoomService.Post method expects a RoomData item as an input parameter. The fields of RoomData (Id and RoomType) are provided in the header row of the Row table. It also returns a RoomData back to FitNessse.

**Note:** SmartHome\FitNesse\lib directory has a log4j2.xml file in it. By default, the file configures log4j to print all the data passing between easyfit and the SUT into the SmartHome\FitNesse\easyfit.log log file. In case of the Row table mentioned above, the data would look like this:

    ... POST http://localhost:56473/CreateRoom.json: {"Id":null,"RoomType":"Kitchen"} -> {"Id":1,"RoomType":"Kitchen"}
    ... POST http://localhost:56473/CreateRoom.json: {"Id":null,"RoomType":"Sauna"} -> {"Id":2,"RoomType":"Sauna"}
    ... POST http://localhost:56473/CreateRoom.json: {"Id":null,"RoomType":"LivingRoom"} -> {"Id":3,"RoomType":"LivingRoom"}

The log shows that there were three POST calls to http://localhost:56473/CreateRoom.json with incoming data on the left side of the arrow and response on the right.

The Id field is null when entering the SUT because the Row table is about to initialize variables $kitchen, $sauna and $livingRoom with values coming from the SUT (1, 2 and 3). This mechanism is similar to FitNesse [symbols](http://localhost:8080/FitNesse.UserGuide.SliM.SymbolsInTables) but it's fully re-implemented in easyfit allowing, for example, variable initialization inside the Query table.

After having executed the suite set-up it looks like this:

![](https://raw.github.com/nikoudel/easyfit/images/SuiteSetupExecuted.PNG "SuiteSetUp executed")

The left-side arrow indicates variable initialization and eg. $kitchen [1] means a variable with value 1 has been used. The data of CreateDevice row table follows:

    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"1","DeviceType":"CeilingLight"} -> {"Id":1,"RoomId":1,"DeviceType":"CeilingLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"1","DeviceType":"LedSpotLight"} -> {"Id":2,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"1","DeviceType":"LedSpotLight"} -> {"Id":3,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"1","DeviceType":"LedSpotLight"} -> {"Id":4,"RoomId":1,"DeviceType":"LedSpotLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"2","DeviceType":"SteamLight"} -> {"Id":5,"RoomId":2,"DeviceType":"SteamLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"3","DeviceType":"CeilingLight"} -> {"Id":6,"RoomId":3,"DeviceType":"CeilingLight","DeviceState":"Undefined"}
    ... POST http://localhost:56473/CreateDevice.json: {"Id":null,"RoomId":"3","DeviceType":"TableLight"} -> {"Id":7,"RoomId":3,"DeviceType":"TableLight","DeviceState":"Undefined"}

[lights]: http://localhost:8080/SmartHome.TurnKitchenLightsOnOff
[ss]: https://servicestack.net