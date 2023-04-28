import java.util.ArrayList;
import java.util.*;

public class MazeSolver implements IMazeSolver {
	private Maze maze;
	private int startRow, startCol, endRow, endCol;
	private HashSet<Room> visited;
	private HashMap<Room, Room> parents;
	private ArrayList<Room> frontier;
	private HashMap<Room, Integer[]> roomRowAndCol;
	private ArrayList<Integer> stepsReachable;
	private int finalStep;
	private boolean solved;
	private static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
	private static int[][] DELTAS = new int[][] {
		{ -1, 0 }, // North
		{ 1, 0 }, // South
		{ 0, 1 }, // East
		{ 0, -1 } // West
	};
	private Room startRoom;

	public MazeSolver() {
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

		// when in doubt, use a HashMap
		this.visited = new HashSet<>();
		// key is the node, val is the parent of the node
		this.parents = new HashMap<>();
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

		solved = true;

		return solve(0);
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

	public Integer solve(int rooms) {
		frontier.add(this.startRoom);
		visited.add(this.startRoom);
		while (!this.frontier.isEmpty()) {
			ArrayList<Room> nextFrontier = new ArrayList<>();
			int size = frontier.size();
			stepsReachable.add(rooms, size);
			for (Room room : frontier) {
				int row = getRow(room);
				int col = getCol(room);
				//if (room.equals(maze.getRoom(endRow, endCol))) return backtrack(room, rooms);
				if (room.equals(maze.getRoom(endRow, endCol))) {
					this.finalStep = rooms;
					this.solved = true;
				}
				addNextFrontier(row, col, nextFrontier);
			}
			rooms++;
			this.frontier = nextFrontier;
		}
		return backtrack(maze.getRoom(endRow, endCol), this.finalStep);
	}

	public Integer backtrack(Room room, int rooms) {
		Room cur = room;
		Room startRoom = maze.getRoom(startRow, startCol);
		if (this.solved) {
			startRoom.onPath = true;
			while (!cur.equals(startRoom)) {
				Room parent = parents.get(cur);
				if (parent == null) return null;
				cur.onPath = true;
				cur = parent;
			}
			System.out.println(maze.getRoom(startRow, startCol).onPath);
			return rooms;
		} else {
			return null;
		}

	}

	public void addNextFrontier(int row, int col, ArrayList<Room> frontier) {
		Room curRoom = maze.getRoom(row, col);
		for (int direction = 0; direction < 4; ++direction) {
			if (canGo(row, col, direction)) { // can we go in that direction?
				// yes we can :)
				int newRow = row + DELTAS[direction][0];
				int newCol = col + DELTAS[direction][1];
				Room nextRoom = maze.getRoom(newRow, newCol);
				if (!visited.contains(nextRoom)) {
					frontier.add(nextRoom);
					visited.add(nextRoom);
					parents.put(nextRoom, curRoom);
					hashRowAndCol(nextRoom, newRow, newCol);
				}
			}
		}
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

	public static void main(String[] args) {
		// Do remember to remove any references to ImprovedMazePrinter before submitting
		// your code!
		try {
			Maze maze = Maze.readMaze("maze-sample.txt");
			IMazeSolver solver = new MazeSolver();
			solver.initialize(maze);

			System.out.println(solver.pathSearch(0, 0, 3, 3));
			ImprovedMazePrinter.printMaze(maze, 0, 0);

			for (int i = 0; i <= 9; ++i) {
				System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
