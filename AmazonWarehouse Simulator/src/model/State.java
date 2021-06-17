package model;

public enum State {
	
	WAITING, //Not being worked on/not touched yet.
	WORKEDON, //Being worked on
	DONE, //Done, ready to be processed (packed)
	DISPATCHED //Order packed. -Not applicable to shelf.

}
