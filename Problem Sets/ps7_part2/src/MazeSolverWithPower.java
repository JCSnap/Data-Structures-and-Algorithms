import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class MazeSolverWithPower implements IMazeSolverWithPower {
	private Maze maze;
	private int startRow, startCol, endRow, endCol;
	private HashMap<Integer, HashSet<Room>> visited;
	private HashSet<Room> globalVisited;
	private HashMap<PowerRoom, PowerRoom> parents;
	private ArrayList<PowerRoom> frontier;
	private HashMap<Room, Integer[]> roomRowAndCol;
	private ArrayList<Integer> stepsReachable;
	private int finalStep;
	private PowerRoom finalPowerRoom;
	private boolean solved;
	private boolean first;
	private Room startRoom;
	private static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
	private static int[][] DELTAS = new int[][] {
		{ -1, 0 }, // North
		{ 1, 0 }, // South
		{ 0, 1 }, // East
		{ 0, -1 } // West
	};

	public MazeSolverWithPower() {
		maze = null;
		solved = false;
	}

	@Override
	public void initialize(Maze maze) {
		this.maze = maze;
		this.solved = false;
	}

	@Override
	public Integer pathSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		if (maze == null) {
			throw new Exception("Oh no! You cannot call me without initializing the maze!");
		}

		if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
				endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
			throw new IllegalArgumentException("Invalid start/end coordinate");
		};

		for (int i = 0; i < maze.getRows(); ++i) {
			for (int j = 0; j < maze.getColumns(); ++j) {
				maze.getRoom(i, j).onPath = false;
			}
		}

		this.visited = new HashMap<>();
		this.globalVisited = new HashSet<>();
		this.parents = new HashMap<>();
		this.frontier = new ArrayList<>();
		this.roomRowAndCol = new HashMap<>();
		this.stepsReachable = new ArrayList<>();
		this.finalStep = 0;

		this.startRow = startRow;
		this.startCol = startCol;
		this.endRow = endRow;
		this.endCol = endCol;

		this.startRoom = maze.getRoom(startRow, startCol);
		hashRowAndCol(this.startRoom, startRow, startCol);

		this.solved = false;
		this.first = true;

		return solve(0, 0);
	}

	private boolean canGo(int row, int col, int dir) {
		// not needed since our maze has a surrounding block of wall
		// but Joe the Average Coder is a defensive coder!
		if (row + DELTAS[dir][0] < 0 || row + DELTAS[dir][0] >= maze.getRows()) return false;
		if (col + DELTAS[dir][1] < 0 || col + DELTAS[dir][1] >= maze.getColumns()) return false;

		switch (dir) {
			case NORTH:
				return !maze.getRoom(row, col).hasNorthWall();
			case SOUTH:
				return !maze.getRoom(row, col).hasSouthWall();
			case EAST:
				return !maze.getRoom(row, col).hasEastWall();
			case WEST:
				return !maze.getRoom(row, col).hasWestWall();
		}

		return false;
	}

	public boolean hasWall(int row, int col, int dir) {
		int newRow = row + DELTAS[dir][0];
		int newCol = col + DELTAS[dir][1];
		// not an empty room
		return !canGo(row, col, dir) &&
				// cannot be border
				!((newRow <0 || newRow > maze.getRows() - 1) ||
						(newCol < 0 || newCol > maze.getColumns() - 1));
	}

	/**
	 * A pair with head representing the superpower state and tail representing the room
	 */
	public class PowerRoom {
		private int power;
		private Room room;
		public PowerRoom(int power, Room room) {
			this.power = power;
			this.room = room;
		}

		public int getPower() {
			return this.power;
		}

		public Room getRoom() {
			return this.room;
		}

		public int getRoomRow() {
			return getRow(this.room);
		}

		public int getRoomCol() {
			return getCol(this.room);
		}

		public boolean equalEndRoom() {
			return this.getRoomRow() == endRow && this.getRoomCol() == endCol;
		}
	}

	public Integer solve(int rooms, int superpowers) {
		// create a hashset for all values of superpowers
		initVisited(superpowers);
		// add starting room to frontier and mark it as visited (base case)
		frontier.add(new PowerRoom(superpowers, this.startRoom));
		addPowerRoomToVisited(superpowers, this.startRoom);
		while (!this.frontier.isEmpty()) {
			ArrayList<PowerRoom> nextFrontier = new ArrayList<>();
			int size = 0;
			for (PowerRoom powerRoom : frontier) {
				int row = powerRoom.getRoomRow();
				int col = powerRoom.getRoomCol();
				// is current room is not visited, add to size
				if (!globalVisited.contains(powerRoom.getRoom())) size++;
				globalVisited.add(powerRoom.getRoom());
				// if this is the first time we reach the destination
				if (powerRoom.equalEndRoom() && this.first) {
					// store everything and then continue, this will only be run once
					this.finalStep = rooms;
					this.finalPowerRoom = powerRoom;
					this.solved = true;
					this.first = false;
				}
				addNextFrontier(powerRoom, row, col, nextFrontier, powerRoom.getPower());
			}
			stepsReachable.add(rooms, size);
			rooms++;
			this.frontier = nextFrontier;
		}
		// to find the successful path and mark them
		return backtrack(this.finalStep);
	}

	public Integer backtrack(int rooms) {
		PowerRoom cur = this.finalPowerRoom;
		Room startRoom = maze.getRoom(startRow, startCol);
		if (this.solved) {
			startRoom.onPath = true;
			while (!cur.getRoom().equals(startRoom)) {
				PowerRoom parent = parents.get(cur);
				if (parent == null) return null;
				cur.getRoom().onPath = true;
				cur = parent;
			}
			return rooms;
		} else {
			return null;
		}
	}

	public void addNextFrontier(PowerRoom curPowerRoom, int row, int col, ArrayList<PowerRoom> frontier,int powerLeft) {
		for (int direction = 0; direction < 4; ++direction) {
			int newRow = row + DELTAS[direction][0];
			int newCol = col + DELTAS[direction][1];
			if (canGo(row, col, direction)) { // can we go in that direction?
				// yes we can :)
				Room nextRoom = maze.getRoom(newRow, newCol);
				if (!visited.get(powerLeft).contains(nextRoom)) {
					PowerRoom nextPowerRoom = new PowerRoom(powerLeft, nextRoom);
					frontier.add(nextPowerRoom);
					addPowerRoomToVisited(powerLeft, nextRoom);
					setPowerRoomParents(nextPowerRoom, curPowerRoom);
					hashRowAndCol(nextRoom, newRow, newCol);
				}
			} else if (hasWall(row, col, direction) && powerLeft > 0) {
				Room nextRoom = maze.getRoom(newRow, newCol);
				// "break" the wall and go to the other room, reducing power by one
				if (!visited.get(powerLeft - 1).contains(nextRoom)) {
					PowerRoom nextPowerRoom = new PowerRoom(powerLeft - 1, nextRoom);
					frontier.add(nextPowerRoom);
					addPowerRoomToVisited(powerLeft - 1, nextRoom);
					setPowerRoomParents(nextPowerRoom, curPowerRoom);
					hashRowAndCol(nextRoom, newRow, newCol);
				}
			}
		}
	}

	public void initVisited(int superpowers) {
		for (int i = 0; i <= superpowers; i++) {
			visited.put(i, new HashSet<Room>());
		}
	}

	public void addPowerRoomToVisited(int superpowers, Room room) {
		HashSet<Room> visitedForCurrentPower = visited.get(superpowers);
		visitedForCurrentPower.add(room);
		visited.put(superpowers, visitedForCurrentPower);
	}

	public void setPowerRoomParents(PowerRoom child, PowerRoom parent) {
		parents.put(child, parent);
	}

	public void hashRowAndCol(Room room, int row, int col) {
		Integer[] arr = new Integer[]{row, col};
		roomRowAndCol.put(room, arr);
	}

	public int getRow(Room room) {
		Integer[] temp = roomRowAndCol.get(room);
		return temp[0];
	}

	public int getCol(Room room) {
		Integer[] temp = roomRowAndCol.get(room);
		return temp[1];
	}

	@Override
	public Integer numReachable(int k) throws Exception {
		if (k < 0) {
			throw new IllegalArgumentException("k has to be larger than or equal to 0! ");
		} else if (k >= this.stepsReachable.size()) {
			return 0;
		} else {
			return this.stepsReachable.get(k);
		}
	}

	@Override
	public Integer pathSearch(int startRow, int startCol, int endRow,
							  int endCol, int superpowers) throws Exception {
		if (maze == null) {
			throw new Exception("Oh no! You cannot call me without initializing the maze!");
		}

		if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
				endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
			throw new IllegalArgumentException("Invalid start/end coordinate");
		};

		for (int i = 0; i < maze.getRows(); ++i) {
			for (int j = 0; j < maze.getColumns(); ++j) {
				maze.getRoom(i, j).onPath = false;
			}
		}

		// key is the superpower remaining, value is a set of visited rooms to keep track of visited
		// separately based on remaining superpowers (state)
		this.visited = new HashMap<>();
		// keep track of visited rooms regardless of the state, for stepsReachable
		this.globalVisited = new HashSet<>();
		// child is a powerRoom, parent is also a powerRoom. Child is key, parent is value.
		this.parents = new HashMap<>();
		// an arraylist of powerRoom
		this.frontier = new ArrayList<>();
		// key is the room, value is an array containing the row and column to optimize time
		this.roomRowAndCol = new HashMap<>();
		// index is the steps, value is the count
		this.stepsReachable = new ArrayList<>();
		// since we are continuing exploration even after we "found" the endpoint, we cannot rely on rooms
		this.finalStep = 0;

		this.startRow = startRow;
		this.startCol = startCol;
		this.endRow = endRow;
		this.endCol = endCol;

		this.startRoom = maze.getRoom(startRow, startCol);
		hashRowAndCol(this.startRoom, startRow, startCol);

		this.solved = false;
		// to only record the first time we reach destination
		this.first = true;

		return solve(0, superpowers);
	}

	public static void main(String[] args) {
		try {
			Maze maze = Maze.readMaze("maze-sample.txt");
			IMazeSolverWithPower solver = new MazeSolverWithPower();
			solver.initialize(maze);

			System.out.println(solver.pathSearch(4, 0, 4, 1, 2));
			MazePrinter.printMaze(maze);

			for (int i = 0; i <= 9; ++i) {
				System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
