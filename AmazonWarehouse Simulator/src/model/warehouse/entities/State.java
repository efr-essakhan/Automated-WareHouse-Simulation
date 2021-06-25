package model.warehouse.entities;

/**
 * 
 * Used in class: Order, Robot. And as a map <State, Shelf> in Class Order.
 * @author xbox_
 *
 */
public enum State {
	
	UNCOLLECTED, //Not being worked on/not touched yet.
	COLLECTING, //Being worked on
	COLLECTED, //Done, ready to be processed (packed)
	DISPATCHED, //Order packed. -Not applicable to shelf or Robot.
	CHARGING //Robot is being charged (or going towards a charging station).
}
