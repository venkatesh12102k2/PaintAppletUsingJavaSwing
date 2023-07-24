package paintApplet;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.RenderingHints;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.event.MouseMotionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Stack;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

class Shape implements Serializable
{
	int x1,x2,y1,y2;
	Color color;
	String shapeName,enteredText;
	
	public Shape(int x1,int y1,int x2,int y2,Color color,String shapeName)
	{
		this.x1=x1;
		this.x2=x2;
		this.y1=y1;
		this.y2=y2;
		this.color=color;
		this.shapeName=shapeName;
	}
	 
	public Shape(int x1,int y1,int x2,int y2,String enteredText,Color color,String shapeName)
	{
		this.x1=x1;
		this.y1=y1;
		this.enteredText = enteredText;
		this.color = color;
		this.shapeName = shapeName;
	}
	
	public int getx1()
	{
		return x1;
	}
	public int getx2()
	{
		return x2;
	}
	public int gety1()
	{
		return y1;
	}
	public int gety2()
	{
		return y2;
	}
	public Color getColor()
	{
		return color;
	}
	public String getShapeName()
	{
		return shapeName;
	}
}



class drawingImage extends JComponent implements MouseListener
{
	Graphics graphics;
	Image image,background;
	Graphics2D imageGraphics;
	int x1,y1,x2,y2,endx,endy;
	String shapeName,enteredText=" ";
	Color color;
	JFrame frame;
	ObjectOutputStream outputStream;
	ObjectInputStream inputStream;
	boolean mouseDraggedd=false;
	boolean imageSaved = false;
	boolean imageOpened = false;
	int arcAngle;
	Stack<Shape> drawnShapes;
	BasicStroke stroke = new BasicStroke((float)1);
	boolean isSomethingDrawn,isArc;
	drawingImage(JFrame frame)
	{
		isSomethingDrawn = false;
		isArc=false;
		this.frame = frame;
		shapeName = "line";
		color = Color.black;
		drawnShapes = new Stack<>();
		setDoubleBuffered(false);
		addMouseListener(this);
		
		
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e)
			{
				mouseDraggedd=true;
				x2 = e.getX();
				y2 = e.getY();
				boolean directionChanged = (x2-x1) == (y2-y1);
				if(directionChanged == false)
				{
					clear();
				}
				
				if(imageGraphics != null)
				{
					imageGraphics.setColor(color);
					switch(shapeName)
					{
					case "line":
						imageGraphics.drawLine(x1,y1,x2,y2);
						break;
					case "ellipse":
						ellipse(x1,y1,x2,y2,imageGraphics);
						break;
					case "rectangle":
						rectangle(x1,y1,x2,y2,imageGraphics);
						break;
					case "arc":
						isArc=true;
						arc(x1,y1,x2,y2,imageGraphics);
						break;
					}
					
					isSomethingDrawn=true;
					
					  	
				}	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
			}
			
		});	
	}
	
	public void displayShapeDetails(String s)
	{
		System.out.println("from "+s);
		for(int i=0;i<drawnShapes.size();i++)
		{
			System.out.println(drawnShapes.elementAt(i).getx1());
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		graphics = g;
		super.paintComponent(g);
		if(imageSaved) return;
		if(image == null)
		{
			image = createImage(getSize().width,getSize().height);
			imageGraphics = (Graphics2D) image.getGraphics();
			imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON );
			clear();
		}
		g.drawImage(image,0,0,null);
		for(int i=0;i<drawnShapes.size();i++)	
		{
			Shape t = drawnShapes.elementAt(i);
			String s = t.getShapeName();
			g.setColor(t.color);
			if(s == "line") g.drawLine(t.getx1(), t.gety1(), t.getx2(), t.gety2());
			else if(s == "ellipse")	ellipse(t.getx1(),t.gety1(),t.getx2(),t.gety2(),(Graphics2D)g);
			else if(s == "rectangle") rectangle(t.getx1(),t.gety1(),t.getx2(),t.gety2(),(Graphics2D)g);
			else if(s == "arc") arc(t.getx1(),t.gety1(),t.getx2(),t.gety2(),(Graphics2D) g);
			else if(s == "text" && t.enteredText!=null)
			{
				g.drawString(t.enteredText, t.getx1(), t.gety1());
			}
		}
	}
	

	public void clear()
	{
		imageGraphics.setPaint(Color.white);
		imageGraphics.fillRect(0, 0, getSize().width, getSize().height);
		imageGraphics.setPaint(Color.black);
		repaint();
		
	}
	
	public void ellipse(int x1,int y1,int x2,int y2,Graphics2D g)
	{
		int startx=0,starty=0,width=0,height=0;
		if(x1<x2 && y1<y2) 
		{
			startx=x1;
			starty=y1;
			width=x2-x1;
			height=y2-y1;
		}
		else if(x2<x1 && y2<y1) 
		{
			startx=x2;
			starty=y2;
			width=x1-x2;
			height=y1-y2;
		}
		else if(x1<x2 && y2<y1) 
		{
			startx=x1;
			starty=y2;
			width=x2-x1;
			height=y1-y2;
		}
		else if(x2<x1 && y1<y2) 
		{
			startx=x2;
			starty=y1;
			width=x1-x2;
			height=y2-y1;
		}
		g.drawOval(startx, starty, width, height);
	}
	
	
	
	public void rectangle(int x1,int y1,int x2,int y2,Graphics2D g)
	{
		int startx=0,starty=0,width=0,height=0;
		if(x1<x2 && y1<y2) 
		{
			startx=x1;
			starty=y1;
			width=x2-x1;
			height=y2-y1;
		}
		else if(x2<x1 && y2<y1) 
		{
			startx=x2;
			starty=y2;
			width=x1-x2;
			height=y1-y2;
		}
		else if(x1<x2 && y2<y1) 
		{
			startx=x1;
			starty=y2;
			width=x2-x1;
			height=y1-y2;
		}
		else if(x2<x1 && y1<y2) 
		{
			startx=x2;
			starty=y1;
			width=x1-x2;
			height=y2-y1;
		}
		g.drawRect(startx, starty, width, height);
	}
	
	public void arc(int x1,int y1,int x2,int y2,Graphics2D g)
	{
		int startx=0,starty=0,width=0,height=0;
		//int angle =(int) Math.atan(y2-y1/x2-x1);
		
		if(x1<x2 && y1<y2) 
		{
			startx=x1;
			starty=y1;
			width=x2-x1;
			height=y2-y1;
		}
		else if(x2<x1 && y2<y1) 
		{
			startx=x2;
			starty=y2;
			width=x1-x2;
			height=y1-y2;
		}
		else if(x1<x2 && y2<y1) 
		{
			startx=x1;
			starty=y2;
			width=x2-x1;
			height=y1-y2;
		}
		else if(x2<x1 && y1<y2) 
		{
			startx=x2;
			starty=y1;
			width=x1-x2;
			height=y2-y1;
		}
		g.drawArc(startx, starty, width, height,0,arcAngle);
	}

	public void setShape(String s)
	{
		shapeName = s;
	}
	public void setColor(Color c)
	{
		color = c;
	}
	public void setArcAngle(int x)
	{
		arcAngle = x;
	}
	public void setEnteredText(String t)
	{
		enteredText = t;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		isSomethingDrawn = false;
		x1 = e.getX();
		y1 = e.getY();
		if(shapeName == "text" && enteredText != null)
		{
				imageGraphics.setColor(color);
				imageGraphics.drawString(enteredText, x1, y1);
				drawnShapes.push(new Shape(x1,y1,x2,y2,enteredText,color,shapeName));
				repaint();
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(shapeName == "text" && enteredText==null) return;
		if(isSomethingDrawn == true)
		{
			drawnShapes.push(new Shape(x1,y1,x2,y2,color,shapeName));
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}

class paintFrame extends JFrame 
{
	JMenuBar mb;
	JMenu shapeMenu,colorMenu;
	JMenuItem line, ellipse,rectangle, arc, text,chooseColor;
	String enteredText;
	int arcAngle;
	
	drawingImage drawImageObject;
	Container container;
	
	paintFrame()
	{
		mb = new JMenuBar();
		colorMenu = new JMenu("Color");
		shapeMenu = new JMenu("Shape");
		line = new JMenuItem("LINE");
		ellipse = new JMenuItem("ELLIPSE");
		rectangle = new JMenuItem("RECTANGLE");
		arc = new JMenuItem("ARC");
		text = new JMenuItem("TEXT");
		drawImageObject = new drawingImage(this);
		chooseColor = new JMenuItem("Choose a color...");
		
		mb.setPreferredSize(new Dimension(0,50));
		setTitle("PAINT");
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setLayout(null);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		setJMenuBar(mb);
		
		
		
		shapeMenu.add(line);
		shapeMenu.add(ellipse);
		shapeMenu.add(rectangle);
		shapeMenu.add(arc);
		shapeMenu.add(text);
		
		line.addActionListener(actionListener);
		ellipse.addActionListener(actionListener);
		rectangle.addActionListener(actionListener);
		arc.addActionListener(actionListener);
		text.addActionListener(actionListener);
		mb.add(shapeMenu);
		mb.add(colorMenu);
		colorMenu.add(chooseColor);
		chooseColor.addActionListener(ae->
		{
			Color c = (JColorChooser.showDialog(null, "Pick a color!!!", Color.BLACK));
			drawImageObject.setColor(c);
		});
		
		container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(drawImageObject, BorderLayout.CENTER);
	}
	
	
	ActionListener actionListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == line) drawImageObject.setShape("line");
			if(e.getSource() == ellipse) drawImageObject.setShape("ellipse");
			if(e.getSource() == rectangle) drawImageObject.setShape("rectangle");
			if(e.getSource() == arc)
			{
				String temp = JOptionPane.showInputDialog(this,"Enter arc angle : ");
				arcAngle = Integer.parseInt(temp==null?"0":temp);
				drawImageObject.setArcAngle(arcAngle);
				drawImageObject.setShape("arc");
			}
			if(e.getSource() == text) 
			{
				enteredText = JOptionPane.showInputDialog(this,"Enter the text : ");
				drawImageObject.setEnteredText(enteredText);
				drawImageObject.setShape("text");
			}
		}
	};
	
}

public class paintUsingSwing
{
	public static void main(String[] args) {
		new paintFrame();
	}
}
