import java.util.*;
import java.util.function.Function;

public class MazeSolver implements IMazeSolver {
	private static final int TRUE_WALL = Integer.MAX_VALUE;
	private static final int EMPTY_SPACE = 0;
	private static final List<Function<Room, Integer>> WALL_FUNCTIONS = Arrays.asList(
			Room::getNorthWall,
			Room::getEastWall,
			Room::getWestWall,
			Room::getSouthWall
	);
	private static final int[][] DELTAS = new int[][] {
			{ -1, 0 }, // North
			{ 0, 1 }, // East
			{ 0, -1 }, // West
			{ 1, 0 } // South
	};

	private Maze maze;
	private HashMap<Room, Integer[]> hashRoomToRowsAndCols;
	private PriorityQueue<WeightedRoom> pq;
	private HashMap<Room, ArrayList<WeightedRoom>> connectedWeightedRooms;
	private HashMap<Integer, WeightedRoom> hashWeightedRoom;
	private HashMap<Integer, ArrayList<WeightedRoom>> sameWeightedRoomButDifferentDirection;
	private int endRow;
	private int endCol;
	private WeightedRoom endWeightedRoom;

	public MazeSolver() {
		maze = null;
	}

	@Override
	public void initialize(Maze maze) {
		this.maze = maze;
		this.hashRoomToRowsAndCols = new HashMap<>();
		this.pq = new PriorityQueue<>();
		this.connectedWeightedRooms = new HashMap<>();
		this.hashWeightedRoom = new HashMap<>();
		this.sameWeightedRoomButDifferentDirection = new HashMap<>();
		this.endWeightedRoom = null;
	}

	@Override
	public Integer pathSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		this.endWeightedRoom = null;
		if (startRow == endRow && startCol == endCol) return 0;
		this.endRow = endRow;
		this.endCol = endCol;
		// model as graph, create and add to priority queue
		initGraphAndPriorityQueue(startRow, startCol);
		while (!pq.isEmpty()) {
			WeightedRoom minWeightedRoom = pq.poll();
			//System.out.println("removed from set: " + minWeightedRoom.getWeight());
			// get all the rooms that it can access
			ArrayList<WeightedRoom> neighbours = connectedWeightedRooms.get(minWeightedRoom.getRoom());
			if (neighbours != null) {
				for (WeightedRoom weightedRoom : neighbours) {
					@SuppressWarnings("unchecked")
					WeightedRoomWithDirection weightedRoomWithDirection = (WeightedRoomWithDirection) weightedRoom;
					if (pq.contains(weightedRoomWithDirection)) relax(minWeightedRoom, weightedRoomWithDirection);
				}
			}
		}
		return this.endWeightedRoom == null
				? null
				: this.endWeightedRoom.getWeight() == Integer.MAX_VALUE
				? null
				: this.endWeightedRoom.getWeight();
	}

	public void initGraphAndPriorityQueue(int startRow, int startCol) {
		// do a breadth first traversal of the maze to convert all rooms into nodes
		Room startRoom = this.maze.getRoom(startRow, startCol);
		WeightedRoom startWeightedRoom = new WeightedRoom(startRoom, 0);
		pq.add(startWeightedRoom);
		roomToRowsAndCols(startRoom, startRow, startCol);
		ArrayList<WeightedRoom> frontier = new ArrayList<>();
		HashSet<Room> visited = new HashSet<>();
		frontier.add(startWeightedRoom);
		visited.add(startRoom);
		while (!frontier.isEmpty()) {
			ArrayList<WeightedRoom> nextFrontier = new ArrayList<>();
			for (WeightedRoom weightedRoom : frontier) {
				int row = getRowByRoom(weightedRoom.getRoom());
				int col = getColbyRoom(weightedRoom.getRoom());
				addNextFrontier(row, col, nextFrontier, visited);
			}
			frontier = nextFrontier;
		}
	}

	public void initGraphAndPriorityQueueButStartFromNegativeOne(int startRow, int startCol) {
		// do a breadth first traversal of the maze to convert all rooms into nodes
		Room startRoom = this.maze.getRoom(startRow, startCol);
		WeightedRoom startWeightedRoom = new WeightedRoom(startRoom, 0);
		pq.add(startWeightedRoom);
		roomToRowsAndCols(startRoom, startRow, startCol);
		ArrayList<WeightedRoom> frontier = new ArrayList<>();
		HashSet<Room> visited = new HashSet<>();
		frontier.add(startWeightedRoom);
		visited.add(startRoom);
		while (!frontier.isEmpty()) {
			ArrayList<WeightedRoom> nextFrontier = new ArrayList<>();
			for (WeightedRoom weightedRoom : frontier) {
				int row = getRowByRoom(weightedRoom.getRoom());
				int col = getColbyRoom(weightedRoom.getRoom());
				addNextFrontier(row, col, nextFrontier, visited);
			}
			frontier = nextFrontier;
		}
	}

	public void roomToRowsAndCols(Room room, int row, int col) {
		Integer[] rowsAndCols = new Integer[] {row, col};
		hashRoomToRowsAndCols.put(room, rowsAndCols);
	}

	public int getRowByRoom(Room room) {
		return hashRoomToRowsAndCols.get(room)[0];
	}

	public int getColbyRoom(Room room) {
		return hashRoomToRowsAndCols.get(room)[1];
	}

	public void addNextFrontier(int row, int col, ArrayList<WeightedRoom> nextFrontier, HashSet<Room> visited) {
		Room curRoom = this.maze.getRoom(row, col);
		for (int direction = 0; direction < 4; direction++) {
			int newRow = row + DELTAS[direction][0];
			int newCol = col + DELTAS[direction][1];
			if (canGo(row, col, direction)) {
				Room nextRoom = maze.getRoom(newRow, newCol);
				roomToRowsAndCols(nextRoom, newRow, newCol);
				WeightedRoom nextWeightedRoom;
				// the same row and col will produce the same key
				int hashCode = cantorPairing(newRow, newCol);
				// we want to keep track of the same room that can be accessed from different directions
				// this is such that we can update their weights altogether later on
				if (hashWeightedRoom.containsKey(hashCode)) {
					nextWeightedRoom = new WeightedRoomWithDirection(nextRoom, Integer.MAX_VALUE, direction);
					this.sameWeightedRoomButDifferentDirection.get(hashCode).add(nextWeightedRoom);
				} else {
					nextWeightedRoom = new WeightedRoomWithDirection(nextRoom, Integer.MAX_VALUE, direction);
					hashWeightedRoom.put(hashCode, nextWeightedRoom);
					sameWeightedRoomButDifferentDirection.put(hashCode, new ArrayList<>());
					this.sameWeightedRoomButDifferentDirection.get(hashCode).add(nextWeightedRoom);
				}
				// add to neighbours if this room can be accessed from the current room
				addDirectedEdge(curRoom, nextWeightedRoom);
				if (!visited.contains(nextRoom)) {
					nextFrontier.add(nextWeightedRoom);
					visited.add(nextRoom);
					pq.add(nextWeightedRoom);
					if (nextWeightedRoom.isEndRoom()) {
						this.endWeightedRoom = nextWeightedRoom;
					}
				}
			}
		}
	}

	public void relax(WeightedRoom minWeightedRoom, WeightedRoomWithDirection weightedRoomWithDirection) {
		int direction = weightedRoomWithDirection.getDirection();
		int fearValue = 0;
		// since the direction is the direction that is used to access this current room, we need to "reverse"
		// it to get the wall value. Eg. if there's a wall x between A and B (A x B), B records "East" since
		// that is the direction used to access B, and to get the value of the wall, we reverse it to "West"
		if (direction == 3) {
			fearValue = weightedRoomWithDirection.getRoom().getNorthWall();
		} else if (direction == 2) {
			fearValue = weightedRoomWithDirection.getRoom().getEastWall();
		} else if (direction == 1) {
			fearValue = weightedRoomWithDirection.getRoom().getWestWall();
		} else if (direction == 0) {
			fearValue = weightedRoomWithDirection.getRoom().getSouthWall();
		}
		if (fearValue == EMPTY_SPACE) fearValue = 1;
		int updatedWeight = minWeightedRoom.getWeight() + fearValue;
		if (updatedWeight < weightedRoomWithDirection.getWeight()) {
			//System.out.println("current node: " + weightedRoomWithDirection + " current weight: " + weightedRoomWithDirection.getWeight() + " adjusted: " + updatedWeight);
			//System.out.println("before update: " + temp.getWeight());
			this.pq.remove(weightedRoomWithDirection);
			weightedRoomWithDirection.updateWeight(updatedWeight);
			//System.out.println("after update: " + temp.getWeight());
			pq.add(weightedRoomWithDirection);
			// the same weighted room can have multiple states, since they can be accessed from multiple directions
			// we have to update all the states
			int hashCode = cantorPairing(getRowByRoom(weightedRoomWithDirection.getRoom()), getColbyRoom(weightedRoomWithDirection.getRoom()));
			ArrayList<WeightedRoom> toUpdate = sameWeightedRoomButDifferentDirection.get(hashCode);
			for (WeightedRoom weightedRoom : toUpdate) {
				weightedRoom.updateWeight(updatedWeight);
			}
		} else {
			//System.out.println("current is less!");
		}
	}

	public void relaxBonus(WeightedRoom minWeightedRoom, WeightedRoomWithDirection weightedRoomWithDirection) {
		int direction = weightedRoomWithDirection.getDirection();
		int fearValue = 0;
		// since the direction is the direction that is used to access this current room, we need to "reverse"
		// it to get the wall value. Eg. if there's a wall x between A and B (A x B), B records "East" since
		// that is the direction used to access B, and to get the value of the wall, we reverse it to "West"
		if (direction == 3) {
			fearValue = weightedRoomWithDirection.getRoom().getNorthWall();
		} else if (direction == 2) {
			fearValue = weightedRoomWithDirection.getRoom().getEastWall();
		} else if (direction == 1) {
			fearValue = weightedRoomWithDirection.getRoom().getWestWall();
		} else if (direction == 0) {
			fearValue = weightedRoomWithDirection.getRoom().getSouthWall();
		}
		int updatedWeight = Integer.max(minWeightedRoom.getWeight(), fearValue);
		if (fearValue == EMPTY_SPACE) updatedWeight = minWeightedRoom.getWeight() + 1;
		if (updatedWeight < weightedRoomWithDirection.getWeight()) {
			//System.out.println("current node: " + weightedRoomWithDirection + " current weight: " + weightedRoomWithDirection.getWeight() + " adjusted: " + updatedWeight);
			//System.out.println("before update: " + temp.getWeight());
			this.pq.remove(weightedRoomWithDirection);
			weightedRoomWithDirection.updateWeight(updatedWeight);
			//System.out.println("after update: " + temp.getWeight());
			pq.add(weightedRoomWithDirection);
			// the same weighted room can have multiple states, since they can be accessed from multiple directions
			// we have to update all the states
			int hashCode = cantorPairing(getRowByRoom(weightedRoomWithDirection.getRoom()), getColbyRoom(weightedRoomWithDirection.getRoom()));
			ArrayList<WeightedRoom> toUpdate = sameWeightedRoomButDifferentDirection.get(hashCode);
			for (WeightedRoom weightedRoom : toUpdate) {
				weightedRoom.updateWeight(updatedWeight);
			}
		} else {
			//System.out.println("current is less!");
		}
	}

	public void relaxBonusButStartFromNegativeOne(WeightedRoom minWeightedRoom, WeightedRoomWithDirection weightedRoomWithDirection) {
		int direction = weightedRoomWithDirection.getDirection();
		int fearValue = 0;
		// since the direction is the direction that is used to access this current room, we need to "reverse"
		// it to get the wall value. Eg. if there's a wall x between A and B (A x B), B records "East" since
		// that is the direction used to access B, and to get the value of the wall, we reverse it to "West"
		if (direction == 3) {
			fearValue = weightedRoomWithDirection.getRoom().getNorthWall();
		} else if (direction == 2) {
			fearValue = weightedRoomWithDirection.getRoom().getEastWall();
		} else if (direction == 1) {
			fearValue = weightedRoomWithDirection.getRoom().getWestWall();
		} else if (direction == 0) {
			fearValue = weightedRoomWithDirection.getRoom().getSouthWall();
		}
		int updatedWeight = Integer.max(minWeightedRoom.getWeight(), fearValue);
		if (fearValue == EMPTY_SPACE) updatedWeight = minWeightedRoom.getWeight() + 1;
		if (minWeightedRoom.getWeight() == 0 && fearValue == EMPTY_SPACE) updatedWeight = 0;
		if (updatedWeight < weightedRoomWithDirection.getWeight()) {
			//System.out.println("current node: " + weightedRoomWithDirection + " current weight: " + weightedRoomWithDirection.getWeight() + " adjusted: " + updatedWeight);
			//System.out.println("before update: " + temp.getWeight());
			this.pq.remove(weightedRoomWithDirection);
			weightedRoomWithDirection.updateWeight(updatedWeight);
			//System.out.println("after update: " + temp.getWeight());
			pq.add(weightedRoomWithDirection);
			// the same weighted room can have multiple states, since they can be accessed from multiple directions
			// we have to update all the states
			int hashCode = cantorPairing(getRowByRoom(weightedRoomWithDirection.getRoom()), getColbyRoom(weightedRoomWithDirection.getRoom()));
			ArrayList<WeightedRoom> toUpdate = sameWeightedRoomButDifferentDirection.get(hashCode);
			for (WeightedRoom weightedRoom : toUpdate) {
				weightedRoom.updateWeight(updatedWeight);
			}
		} else {
			//System.out.println("current is less!");
		}
	}

	private boolean isSpecialRoom(WeightedRoom room, int sRow, int sCol) {
		return getRowByRoom(room.getRoom()) == sRow && getColbyRoom(room.getRoom()) == sCol;
	}

	public class WeightedRoom implements Comparable<WeightedRoom> {
		private Room room;
		private int weight;
		public WeightedRoom(Room room, int weight) {
			this.room = room;
			this.weight = weight;
		}

		public int getWeight() {
			return this.weight;
		}

		public void updateWeight(int newWeight) {
			this.weight = newWeight;
		}

		public Room getRoom() {
			return this.room;
		}

		public boolean isEndRoom() {
			return getRowByRoom(this.room) == endRow && getColbyRoom(this.room) == endCol;
		}

		// for the priority queue to compare. It is a minimum if it has the least weight
		@Override
		public int compareTo(WeightedRoom weightedRoom) {
			return this.weight - weightedRoom.getWeight();
		}

		@Override
		public String toString() {
			return "Room - row: " + getRowByRoom(this.room) + " col: " + getColbyRoom(this.room) + " weight: " + this.weight;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof WeightedRoom) {
				return this.room.equals(((WeightedRoom) obj).getRoom());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(room);
		}
	}

	public class WeightedRoomWithDirection extends WeightedRoom {
		private int direction;
		public WeightedRoomWithDirection(Room room, int weight, int direction) {
			super(room, weight);
			this.direction = direction;
		}

		public int getDirection() {
			return this.direction;
		}
	}

	public int cantorPairing(int row, int col) {
		return ((row + col) * (row + col + 1)) / 2 + col;
	}

	// modified from previous parts. You can go even if it is a wall, as long as it is not outside the maze
	// or a true wall
	public boolean canGo(int row, int col, int direction) {
		if (row + DELTAS[direction][0] < 0 || row + DELTAS[direction][0] >= maze.getRows()) return false;
		if (col + DELTAS[direction][1] < 0 || col + DELTAS[direction][1] >= maze.getColumns()) return false;
		switch (direction) {
			case 0: // NORTH
				return maze.getRoom(row, col).getNorthWall() != TRUE_WALL;
			case 1: // EAST
				return maze.getRoom(row, col).getEastWall() != TRUE_WALL;
			case 2: // WEST
				return maze.getRoom(row, col).getWestWall() != TRUE_WALL;
			case 3: // SOUTH
				return maze.getRoom(row, col).getSouthWall() != TRUE_WALL;
		}
		return false;
	}

	// add to neighbours
	public void addDirectedEdge(Room curRoom, WeightedRoom nextWeightedRoom) {
		if (connectedWeightedRooms.containsKey(curRoom)) {
			ArrayList<WeightedRoom> neighbours = connectedWeightedRooms.get(curRoom);
			neighbours.add(nextWeightedRoom);
			connectedWeightedRooms.put(curRoom, neighbours);
		} else {
			ArrayList<WeightedRoom> neighbours = new ArrayList<>();
			neighbours.add(nextWeightedRoom);
			connectedWeightedRooms.put(curRoom, neighbours);
		}
	}

	@Override
	public Integer bonusSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		initialize(this.maze);
		if (startRow == endRow && startCol == endCol) return 0;
		this.endRow = endRow;
		this.endCol = endCol;
		initGraphAndPriorityQueue(startRow, startCol);
		while (!pq.isEmpty()) {
			WeightedRoom minWeightedRoom = pq.poll();
			ArrayList<WeightedRoom> neighbours = connectedWeightedRooms.get(minWeightedRoom.getRoom());
			if (neighbours != null) {
				for (WeightedRoom weightedRoom : neighbours) {
					@SuppressWarnings("unchecked")
					WeightedRoomWithDirection weightedRoomWithDirection = (WeightedRoomWithDirection) weightedRoom;
					if (pq.contains(weightedRoomWithDirection)) relaxBonus(minWeightedRoom, weightedRoomWithDirection);
				}
			}
		}
		return this.endWeightedRoom == null
				? null
				: this.endWeightedRoom.getWeight() == Integer.MAX_VALUE
				? null
				: this.endWeightedRoom.getWeight();
	}

	public Integer bonusSearchButStartFromNegativeOne(int startRow, int startCol, int endRow, int endCol) throws Exception {
		initialize(this.maze);
		if (startRow == endRow && startCol == endCol) return 0;
		this.endRow = endRow;
		this.endCol = endCol;
		initGraphAndPriorityQueueButStartFromNegativeOne(startRow, startCol);
		while (!pq.isEmpty()) {
			WeightedRoom minWeightedRoom = pq.poll();
			ArrayList<WeightedRoom> neighbours = connectedWeightedRooms.get(minWeightedRoom.getRoom());
			if (neighbours != null) {
				for (WeightedRoom weightedRoom : neighbours) {
					@SuppressWarnings("unchecked")
					WeightedRoomWithDirection weightedRoomWithDirection = (WeightedRoomWithDirection) weightedRoom;
					if (pq.contains(weightedRoomWithDirection)) relaxBonusButStartFromNegativeOne(minWeightedRoom, weightedRoomWithDirection);
				}
			}
		}
		return this.endWeightedRoom == null
				? null
				: this.endWeightedRoom.getWeight() == Integer.MAX_VALUE
				? null
				: this.endWeightedRoom.getWeight();
	}

	@Override
	public Integer bonusSearch(int startRow, int startCol, int endRow, int endCol, int sRow, int sCol) throws Exception {
		Integer fromSpecialRoom = bonusSearchButStartFromNegativeOne(sRow, sCol, endRow, endCol);
		Integer fromStartToSpecial = bonusSearch(startRow, startCol, sRow, sCol);
		Integer fromStartToEnd = bonusSearch(startRow, startCol, endRow, endCol);
		if (fromStartToSpecial != null && sRow == endRow && sCol == endCol) return -1;
		if (startRow == endRow && startCol == endCol) return 0;
		// if there exists a path from start through special to the end
		if (fromStartToSpecial != null && fromSpecialRoom != null) {
			//System.out.println(fromStartToSpecial + " " + fromSpecialRoom);
			// if using the
			return fromSpecialRoom < fromStartToEnd
					? fromSpecialRoom
					: fromStartToEnd;
		}
		return fromStartToEnd;
	}

	public static void main(String[] args) {
		try {
			Maze maze = Maze.readMaze("haunted-maze-simple.txt");
			IMazeSolver solver = new MazeSolver();
			solver.initialize(maze);
			//System.out.println(solver.bonusSearch(0, 0, 0, 3, 0, 4));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
