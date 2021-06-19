package model.warehouse;

public enum State {
	
	UNCOLLECTED, //Not being worked on/not touched yet.
	COLLECTING, //Being worked on
	COLLECTED, //Done, ready to be processed (packed)
	DISPATCHED //Order packed. -Not applicable to shelf.

}
