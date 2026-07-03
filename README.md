# VEsNA-SARL

VEsNA-SARL is a framework that enables SARL agents to be embodied inside a virtual environment. This repository contains the bridge between agent minds and agent bodies. VEsNA-SARL was built to extend VEsNA from the single use of JaCaMo agents to allow the use of SARL agents (and both together) as well.

![](./docs/vesna.gif)

## Usage

> [!IMPORTANT]
>
> **Requirements**
>
> - Java  21;
> - Gradle (tested with version 8);
> - Godot 4;
> - SARL 0.15.1 .

## Creating a VEsNA agent in SARL

### Infrastructure and initialization

The program starts in Boot.sarl which spawns the bridge agent which will be the agent connecting the actual agents to the virtual environment by sending and receiving JSON messages.

The bridge agent extends VesnaAgent which calls methods to spawn the actual agents and setup the environment by initializing any needed structure and variable. It uses an event to listen to the agents that to register to a port to connect to their virtual bodies.

### Agent hierarchy & logic

Officer.sarl extends TemplateAgent.sarl and adds more parameters through getAdditionalData(), it also sets more skills and listens to an event that receives a JSON message from the body telling the mind how to update it's parameters (like current location) letting it know if it still needs to move or has reached the destination.

It also listens to an event TaskCompleted telling the agent what to do based on the currentTask.

The individual agents like Alice.sarl can extend officer to add more personalized behaviour, they also initialize the agents by starting the first task; Officer is an abstract agent so we need real agents extending it, if you wanted to have the same behaviour for each agent you could also remove the abstract keyword from Officer and add the `on Initialize` starting the first task in it.

### Communication and events

The events needed to make the bridge work are: SendRequest, to send a request to the body, AnswerReceived to listen to the answer from the body, RegisterAgent to connect an agent to its desired port creating a websocket connection to its body.

The office also implements new events to request a new task and tell when it's completed, along with specific events used for the coffee cup.

### Navigation and capabilities

The OfficeMap implements the connection between each location of the godot map for the office along with some methods to find the next correct step to take to move towards the destination.

The skills include the navigation skill to compute the next step towards the destinations along with a skills for the working logic and a skill to use the coffee machine.

The capacities declare the methods that the skills must implement.

## Creating the VEsNA agent body

To implement your VEsNA body you should implement a websocket Server. The server will receive these messages:

```JSON
{
    sender: "ag_name",
    receiver: "body",
    type: "msg_type",
    data: {
        type: "inner_type",
        ...
    }
}
```

The `sender` is set to the agent name in the mas.

`msg_type` can be the type of action you implement, like `walk`. 

The `inner_type` is the inner type of the action (like `goto`).

## Walk message data

The data field for `goto` as the `inner_type` is:

```JSON
{
 	type: "goto",
    target: "target",
    id: 0 [optional]
}
```

The `target` is the destination you want the body to go to with that JSON message.

## How to run

In order to run the agents you should:
1. import everything from this repo in sarlide (sarl's ide) and add your agents;
2. create or import a scene in Godot (or Unity or another virtual environment) that implements the map and the body to receive the JSON messages as described above;
3. start the main scene in godot with `F5`;
4. right click on Boot.sarl in sarl/vesna/agents -> `run` -> `as SARL agent`.

Sometimes after editing some code and saving the ide may give false errors that will prevent the agents from running, in the upper toolbar click on `project` -> `clean...` -> select the project -> `clean`.

If this still doesn't get rid of the errors then they're either real errors to be fixed or the ide needs to be restarted.

### You can also run SARL agents alongside JaCaMo agents

1. create a Godot scene (or other) and run it;
2. create your own jason agents;
3. launch the agents from command line with `gradle run` by using a `build.gradle` file.

Be careful not to start JaCaMo agents with a port already being used by a SARL agent (and viceversa). You can start the agents in either order.