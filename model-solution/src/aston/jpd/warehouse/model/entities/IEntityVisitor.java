package aston.jpd.warehouse.model.entities;

/**
 * Visitor interface for entities: allows for invoking code based on two types,
 * without using <code>instanceOf</code>.
 */
public interface IEntityVisitor {

	void visit(ChargingPod chargingPod);

	void visit(PackingStation packingStation);

	void visit(Robot robot);

	void visit(StorageShelf shelf);
}
