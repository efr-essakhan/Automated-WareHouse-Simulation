package aston.jpd.warehouse.model.entities;

import aston.jpd.warehouse.model.SimulationException;

/**
 * Entity that tops up the batteries of any robots in its same location. To
 * simplify things, each charging pod is assigned a specific robot.
 */
public class ChargingPod extends Entity {

	public static final int DEFAULT_CHARGING_SPEED = 1;

	private final Robot robot;
	private int chargingSpeed = DEFAULT_CHARGING_SPEED;

	public ChargingPod(String id, Robot r) {
		super(id);

		if (r == null) {
			throw new IllegalArgumentException("Robot must not be null");
		}
		this.robot = r;
	}

	public Robot getRobot() {
		return robot;
	}

	public int getChargingSpeed() {
		return chargingSpeed;
	}

	public void setChargingSpeed(int chargingSpeed) {
		this.chargingSpeed = chargingSpeed;
	}

	@Override
	public boolean canCoexist(IEntity other) {
		// NOTE: this is a simplification - we allow other robots to pass by, but they don't charge here
		return other instanceof Robot;
	}

	@Override
	public void tick() throws SimulationException {
		for (IEntity entity : getEntitiesInSamePosition()) {
			if (entity instanceof Robot) {
				Robot r = (Robot) entity;
				if (r == this.robot) {
					final int oldValue = r.currentChargeProperty().get();
					final int newValue = Math.min(r.getMaximumCharge(), oldValue + chargingSpeed);
					r.currentChargeProperty().set(newValue);
				}
			}
		}
	}

	@Override
	public void accept(IEntityVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + chargingSpeed;
		result = prime * result + ((robot == null) ? 0 : robot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChargingPod other = (ChargingPod) obj;
		if (chargingSpeed != other.chargingSpeed)
			return false;
		if (robot == null) {
			if (other.robot != null)
				return false;
		} else if (!robot.equals(other.robot))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChargingPod [");
		if (robot != null) {
			builder.append("robot=");
			builder.append(robot);
			builder.append(", ");
		}
		builder.append("chargingSpeed=");
		builder.append(chargingSpeed);
		builder.append(", ");
		if (getIdentifier() != null) {
			builder.append("getIdentifier()=");
			builder.append(getIdentifier());
		}
		builder.append("]");
		return builder.toString();
	}

	
}
