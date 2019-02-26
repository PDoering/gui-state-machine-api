# GUI State Machine API

API for the creation and modification of nondeterministic finite automaton for the automatic generation of GUI tests with the help of a genetic algorithm.
This is a small code example of creating a new state machine and adding two states connected with a transition:
```scala
import de.retest.guistatemachine.api.impl.GuiStateMachineApiImpl
import de.retest.ui.descriptors.SutState
import de.retest.surili.model.actions.NavigateToAction

val guiStateMachineApi = new GuiStateMachineApiImpl
val stateMachineId = guiStateMachineApi.createStateMachine()
val stateMachine = guiStateMachineApi.getStateMachine(stateMachineId).get
val currentState = new SutState(currentDescriptors)
val action = new NavigateToAction("http://google.com")
val nextState = new SutState(nextDescriptors)
stateMachine.executeAction(currentState, action, nextState)
stateMachine.saveGML("mystatemachine.gml")
stateMachine.save("mystatemachine.ser")
```

State machines can be saved as on loaded from files.
Besides, they can be saved as [GML](https://en.wikipedia.org/wiki/Graph_Modelling_Language) files which can be visualized by editors like [yEd](https://www.yworks.com/products/yed).

## Automatic Build with TravisCI

[![Build Status](https://travis-ci.com/retest/gui-state-machine-api.svg?branch=master)](https://travis-ci.com/retest/gui-state-machine-api)
[![Code Coverage](https://img.shields.io/codecov/c/github/retest/gui-state-machine-api/master.svg)](https://codecov.io/github/retest/gui-state-machine-api?branch=master)

## Build Credentials

Define the Nexus password in the environment variable `TRAVIS_NEXUS_PW`.
Otherwise, the build will fail!

## SBT Commands

* `sbt compile` to build the project manually.
* `sbt assembly` to create a standalone JAR which includes all dependencies including the Scala libraries. The standalone JAR is generated as `target/scala-<scalaversion>/gui-state-machine-api-assembly-<version>.jar`.
* `sbt eclipse` to generate a project for Eclipse.
* `sbt test` to execute all unit tests.
* `sbt coverage` to generate coverage data.
* `sbt coverageReport` to generate a HTML coverage report.
* `sbt scalastyle` to make a check with ScalaStyle.
* `sbt doc` to generate the scaladoc API documentation.
* `sbt scalafmt` to format the Scala source files with scalafmt.
* `sbt 'release cross with-defaults'` to create a release with a new version number which is added as tag. This command does also publish the artifacts.
* `sbt publish` publishes the artifacts in ReTest's Nexus. Requires a `$HOME/.sbt/.credentials` file with the correct credentials. This command can be useful to publish SNAPSHOT versions.

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

## Concurrency

The creation and modification of state machines should be threadsafe.
