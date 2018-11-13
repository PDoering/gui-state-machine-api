# GUI State Machine API

API for the creation and modification of nondeterministic finite automaton for the automatic generation of GUI tests with the help of a genetic algorithm.
Basically, it does only provide only the two calls `getState` and `executeAction`.

## Automatic Build with TravisCI
[![Build Status](https://travis-ci.org/retest/gui-state-machine-api.svg?branch=master)](https://travis-ci.org/retest/gui-state-machine-api)
[![Code Coverage](https://img.shields.io/codecov/c/github/retest/gui-state-machine-api/master.svg)](https://codecov.io/github/retest/gui-state-machine-api?branch=master)

## SBT Commands
* `sbt compile` to build the project manually.
* `sbt run` to start the REST service.
* `sbt assembly` to create a standalone JAR which includes all dependencies including the Scala libraries. The standalone JAR is generated as `target/scala-<scalaversion>/gui-state-machine-api-assembly-<version>.jar`.
* `sbt eclipse` to generate a project for Eclipse.
* `sbt test` to execute all unit tests.
* `sbt coverage` to generate coverage data.
* `sbt coverageReport` to generate a HTML coverage report.
* `sbt scalastyle` to make a check with ScalaStyle.
* `sbt doc` to generate the scaladoc API documentation.
* `sbt scalafmt` to format the Scala source files with scalafmt.
* `sbt release with-defaults` to create a release with a new version number which is published to a repository.

## NFA for the Representation of Tests
A nondeterministic finite automaton represents the states of the GUI during the test.
The actions executed by the user on the widgets are represented by transitions.
If an action has not been executed yet from a state, it leads to an unknown state.
The unknown state is a special state from which all actions could be executed.
The NFA is based on the UI model from [Search-Based System Testing: High Coverage, No False Alarms](http://www.specmate.org/papers/2012-07-Search-basedSystemTesting-HighCoverageNoFalseAlarms.pdf) (section "4.5 UI Model").
Whenever an unknown state is replaced by a newly discovered state, the NFA has to be updated.

The NFA is used to generate test cases (sequence of UI actions) with the help of a genetic algorithm.
For example, whenever a random action is executed with the help of monkey testing, it adds a transition to the state machine.
After running the genetic algorithm, the state machine is then used to create a test suite.

**At the moment, the following definitions are incomplete and must be adapted to the actual implementation which calls this service.**

### Test Suite
A set of test cases.

### Test Case
A sequence of UI actions.

### UI Action
An action which can be triggered by the user via the GUI.

### UI Path
A sequence of states with transitions from one state to another.
Each transition is a UI action.

### State
A state is defined by the set of all visible and interactable windows together with their enabled widgets.

## Scala API for GUI State Machines
The package [api](./src/main/scala/de/retest/guistatemachine/api/) contains all types and methods for getting and modifying the GUI state machine.

## REST API
At the moment there is only an initial version of a REST API which has to be mapped to the Scala API.
Some suggestions how the REST API for the state machine could look like:
* `/state-machines` GET queries all existing state machines.
* `/create-state-machine` POST creates a new state machine.
* `/state-machine/<long>` GET queries an existing state machine.
* `/state-machine/<long>/states` GET queries all existing states of the state machine.
* `/state-machine/<long>/state/<long>` GET queries a specific state of the state machine which contains transitions.
* `/state-machine/<long>/state/<long>/transitions` GET queries all transitions of a specific state.
* `/state-machine/<long>/state/<long>/transition/<long>` GET queries a specific transition of a specific state.
* `/state-machine/<long>/execute` POST executes the passed action from the passed state which might lead to a new state and adds a transition to the state machine. The action must be part of all actions?

### Bash Scripts for REST Calls
The directory [scripts](./scripts) contains a number of Bash scripts which use `curl` to send REST calls to a running server.