# VEsNA-light

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

### Making a VEsNA agent in SARL

Create a new .sarl agent for the physical body of the new agent, the agent should extend Officer.sarl, refer to agents Alice.sarl, Bob.sarl and so on.
Once done you may change Officer.sarl's methods however you please to change the way the agent will act.

Note that most of the agent's skills are in the Skill file so if you want to drastically change something you may want to create a new skill (in a separate file or the same one) that extends an already existing one to change the methods and then install that skill to your agent

The agent class BridgeAgent creates a connection between each agent and its body. The body implements a server with an address and a port.

### Making the VEsNA agent body

To implement your VEsNA body you should implement a websocket Server. The server will receive these messages:

```json
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

The `sender` is set to the agent name in the mas. `msg_type` can be `walk`, `rotate` or `jump`. The `inner_type` is the inner type of the action.

Jump action has an empty data field.

Really only `walk` is used by SARL agents.

#### Walk message data

The data field for `goto` is:

```json
{
 	type: "goto",
    target: "target",
    id: 0 [optional]
}
```

## Try the playground

In order to try the playground, you should:
1. open sarlide (sarl's ide) and import everything from this repo;
2. open Godot and import the playground (in case of a different playground do change the agents' map);
3. start the main scene in godot with `F5`;
4. right click on Boot.sarl in in sarl/vesna/agents -> run -> as SARL agent;

Sometimes after modifying some code and saving the ide may give false errors that will prevent the agents from running, in the upper toolbar click on project -> clean... -> select the project -> clean.
If this still doesn't get rid of the errors then they're probably real errors that need to be fixed.

### You can also run SARL agents alongside JaCaMo agents

1. open Godot and import the playground you want (unless already open from sarl running);
2. start the main scene (unless already running from sarl);
3. go in the mind folder;
4. launch the project (with `gradle run`).

Be careful not to start JaCaMo agents with a port already being used by a SARL agent (and viceversa)