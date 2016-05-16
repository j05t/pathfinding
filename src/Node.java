import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Node extends JButton implements MouseListener {
	public int distance, m, n;
	public Node predecessor;
	public boolean isStart, isGoal, isBlock, traversed;

	public Node(int m, int n) {
		this.m = m;
		this.n = n;
		distance = Integer.MAX_VALUE;
		addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && !isBlock && !isGoal && !PathFinding.STARTSET) {
			PathFinding.STARTSET = true;
			setBackground(Color.GREEN);
			isStart = true;
		} else if (e.getButton() == MouseEvent.BUTTON3 && !isBlock && !isStart && !PathFinding.GOALSET) {
			PathFinding.GOALSET = true;
			setBackground(Color.RED);
			isGoal = true;
		} else if (e.getButton() == MouseEvent.BUTTON2 && !isBlock && !isStart && !isGoal) {
			setBackground(Color.BLUE);
			isBlock = true;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.isAltDown() && !isBlock && !isStart && !isGoal) {
			setBackground(Color.BLUE);
			isBlock = true;
		}
	}

	public void reset() {
		isStart = false;
		isGoal = false;
		isBlock = false;
		traversed = false;
		distance = Integer.MAX_VALUE;
		setBackground(null);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}