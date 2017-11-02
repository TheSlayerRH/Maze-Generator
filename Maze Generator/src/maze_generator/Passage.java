package maze_generator;

public class Passage {
	private boolean northWall, eastWall, southWall, westWall;
	
	public Passage(){
		northWall = false;
		eastWall = false;
		southWall = false;
		westWall = false;
	}

	public boolean hasNorthWall() {
		return northWall;
	}

	public void setNorthWall(boolean northWall) {
		this.northWall = northWall;
	}

	public boolean hasEastWall() {
		return eastWall;
	}

	public void setEastWall(boolean eastWall) {
		this.eastWall = eastWall;
	}

	public boolean hasSouthWall() {
		return southWall;
	}

	public void setSouthWall(boolean southWall) {
		this.southWall = southWall;
	}

	public boolean hasWestWall() {
		return westWall;
	}

	public void setWestWall(boolean westWall) {
		this.westWall = westWall;
	}
	
	
	
}
