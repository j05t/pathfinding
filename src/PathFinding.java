import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PathFinding extends JFrame {

	public static boolean STARTSET = false;
	public static boolean GOALSET = false;

	private static Node START, GOAL;

	private ArrayList<Node> nodes;
	private ArrayList<Node> reachableNodes;
	private JPanel p;

	public static void main(String args[]) {
		// set grid size here
		int m = 48, n = 48;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new PathFinding(m, n).setVisible(true);
			}
		});
	}

	public PathFinding(int m, int n) {

		super("FindPath");
		p = new JPanel();
		nodes = new ArrayList<Node>();
		reachableNodes = new ArrayList<Node>();

		createMenuBar();
		setSize(800, 800);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		p.setLayout(new GridLayout(m, n));

		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++) {
				Node node = new Node(i, j);
				nodes.add(node);
				p.add(node);
			}

		add(p);
	}

	private boolean init() {
		START = getStartNode();
		GOAL = getGoalNode();
		START.distance = 0;
		getReachableNodes(START);

		if (!reachableNodes.contains(GOAL)) {
			JOptionPane.showMessageDialog(null, "No route to goal node", "Error", JOptionPane.PLAIN_MESSAGE);
			return false;
		} else
			return true;
	}

	private void dijkstra() {
		int nodesExplored = 0;
		Node u = null;

		if (!init())
			return;

		do {
			nodesExplored++;
			u = extract_min(reachableNodes);

			if (u != null && !u.isGoal && !u.isStart && !u.isBlock)
				u.setBackground(Color.PINK);

			for (Node v : getNeighbors(u))
				relax(u, v);

		} while (u != null && !u.isGoal);

		doneMessage(nodesExplored);
	}

	private void aStar() {
		int nodesExplored = 0;
		Node u = null;

		if (!init())
			return;

		do {
			nodesExplored++;
			u = extract_min_with_heuristics(reachableNodes);

			if (u != null && !u.isGoal && !u.isStart && !u.isBlock)
				u.setBackground(Color.PINK);

			for (Node v : getNeighbors(u))
				relax(u, v);

		} while (u != null && !u.isGoal);

		doneMessage(nodesExplored);
	}

	private void doneMessage(int nodesExplored) {
		colorPath(Color.CYAN);
		JOptionPane.showMessageDialog(null, "Nodes explored: " + nodesExplored + " Distance: " + GOAL.distance,
				"Finished", JOptionPane.PLAIN_MESSAGE);
	}

	private void colorPath(Color color) {
		Node curNode = GOAL.predecessor;

		while (curNode != null && curNode != START) {
			curNode.setBackground(color);
			curNode = curNode.predecessor;
		}
	}

	private void getReachableNodes(Node start) {
		for (Node node : getNeighbors(start)) {
			if (!node.traversed && !node.isBlock) {
				node.traversed = true;
				reachableNodes.add(node);
				getReachableNodes(node);
			}
		}
	}

	private Node extract_min(ArrayList<Node> unvisitedNodes) {
		Node min = unvisitedNodes.get(0);
		for (Node node : unvisitedNodes) {
			if (node.distance < min.distance)
				min = node;
		}
		unvisitedNodes.remove(min);
		return min;
	}

	private Node extract_min_with_heuristics(ArrayList<Node> unvisitedNodes) {
		Node min = unvisitedNodes.get(0);
		double minDist = min.distance + getHeuristic(min);

		for (Node node : unvisitedNodes) {
			if (node.distance + getHeuristic(node) < minDist) {
				min = node;
				minDist = min.distance + getHeuristic(min);
			}
		}
		unvisitedNodes.remove(min);
		return min;
	}

	private double getHeuristic(Node node) {
		int rows_away = Math.abs(GOAL.m - node.m);
		int cols_away = Math.abs(GOAL.n - node.n);

		// admissible heuristic - don't overestimate the cost of reaching the goal
		return Math.min(rows_away, cols_away) * 10 + Math.max(rows_away, cols_away) * 6;
	}

	private void relax(Node u, Node v) {
		// horizontal or vertical move
		if (u.m == v.m || u.n == v.n) {
			if (u.distance + 10 < v.distance) {
				v.distance = u.distance + 10;
				v.predecessor = u;
			}
		} else { // diagonal move
			if (u.distance + 14 < v.distance) {
				v.distance = u.distance + 14;
				v.predecessor = u;
			}
		}
	}

	private Node getStartNode() {
		if (PathFinding.STARTSET)
			for (Node node : nodes)
				if (node.isStart)
					return node;
		return null;
	}

	private Node getGoalNode() {
		if (PathFinding.GOALSET)
			for (Node node : nodes)
				if (node.isGoal)
					return node;
		return null;
	}

	private ArrayList<Node> getNeighbors(Node node) {
		ArrayList<Node> neighbors = new ArrayList<Node>();

		for (Node curNode : nodes)
			if (!node.isBlock) {
				if (curNode.m == node.m) { // left and right neighbors
					if (curNode.n == node.n - 1 || curNode.n == node.n + 1)
						neighbors.add(curNode);
				} else if (curNode.m == node.m - 1) { // upper neighbors
					if (curNode.n == node.n || curNode.n == node.n - 1 || curNode.n == node.n + 1)
						neighbors.add(curNode);
				} else if (curNode.m == node.m + 1) { // lower neighbors
					if (curNode.n == node.n || curNode.n == node.n - 1 || curNode.n == node.n + 1)
						neighbors.add(curNode);
				}
			}
		return neighbors;
	}

	private void createMenuBar() {
		String helptitle = "Java implementation of the Dijkstra and A* algorithms";
		String helpmessage = "Set the start node with left mouse button.\n"
				+ "Set the goal node with right mouse button.\n\n" 
				+ "Set obstacle with middle mouse button or\n"
				+ "by moving your mouse while holding ALT.";

		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("Find Path");
		JMenu help = new JMenu("Help");
		JMenuItem dMenuItem = new JMenuItem("Dijkstra");
		JMenuItem aStarMenuItem = new JMenuItem("A*");
		JMenuItem resetMenuItem = new JMenuItem("Reset");
		JMenuItem helpMenuItem = new JMenuItem("About");

		dMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (STARTSET && GOALSET)
					dijkstra();
				else
					JOptionPane.showMessageDialog(null, "Set start and goal first!", "Error",
							JOptionPane.PLAIN_MESSAGE);
			}
		});

		aStarMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (STARTSET && GOALSET)
					aStar();
				else
					JOptionPane.showMessageDialog(null, "Set start and goal first!", "Error",
							JOptionPane.PLAIN_MESSAGE);
			}
		});

		resetMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GOALSET = false;
				STARTSET = false;

				for (Node node : nodes)
					node.reset();
			}
		});

		helpMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, helpmessage, helptitle, JOptionPane.PLAIN_MESSAGE);
			}
		});

		file.add(dMenuItem);
		file.add(aStarMenuItem);
		file.add(resetMenuItem);
		help.add(helpMenuItem);
		menubar.add(file);
		menubar.add(help);
		setJMenuBar(menubar);
	}
}
