package lab4;
import java.util.*;


abstract class Animal
{
	private String name;
	private int timestamp;
	private int health;
	private int xCoordinate;
	private int yCoordinate;
	private int Capacity;
	public int counter;
	Animal(int t,int h,int x,int y,int c,String s)
	{
		this.name = s;
		timestamp = t;
		health = h;
		xCoordinate = x;
		yCoordinate = y;
		Capacity = c;
	}
	public boolean equals(Object o)
	{
		if (o instanceof Animal)
		{
			o = (Animal)o;
			return this.getName().equals(((Animal) o).getName());
		}
		return false;
	}
	public String toString()
	{
		return timestamp+" health";
	}
	public String getName()
	{
		return name;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getxCoordinate() {
		return xCoordinate;
	}
	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}
	public int getyCoordinate() {
		return yCoordinate;
	}
	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}
	public int  getCapacity()
	{
		return this.Capacity;
	}
	public abstract Animal takeTurn(Animal h, int c_counter,Grasslands inside,Grasslands g1,Grasslands g2,Animal[] animals,PriorityQueue<Animal> q);
	public int distance(int x1,int y1,int x2,int y2)
	{
		double val1 = Math.pow(x1-x2,2);
		double val2 = Math.pow(y1-y2,2);
		double fd = Math.pow(val1+val2,.5);
		return Math.round((float)fd);
	}
	public  Animal nearAnimal(Animal a,Animal b)
	{
		int d1 = distance(this.getxCoordinate(),this.getyCoordinate(),a.getxCoordinate(),a.getyCoordinate());
		int d2 = distance(this.getxCoordinate(),this.getyCoordinate(),b.getxCoordinate(),b.getyCoordinate());
		if (d1>d2)
			return b;
		return a;
	}
	public void shift(int d,int x,int y)
	{
		double m = ((double)(y-this.getyCoordinate()))/(double)(x-this.getxCoordinate());
		double theta = Math.toRadians(Math.atan(m));
		double sin = Math.sin(theta);
		double cos = Math.cos(theta);
		double a = this.getxCoordinate()+cos*d;
		double b = this.getyCoordinate()+sin*d;
		a = Math.round(a);
		b = Math.round(b);
		this.setxCoordinate((int)a);
		this.setyCoordinate((int)b);
	}
}


class Herbivorus extends Animal
{
	Herbivorus(String name,int t,int h,int x,int y,int e)
	{
		super(t,h,x,y,e,name);
	}
	
	
	public Animal takeTurn(Animal h, int c_counter,Grasslands inside,Grasslands g1,Grasslands g2,Animal[] animals,PriorityQueue<Animal> q)
	{
		if (h.counter>=7)
			h.setHealth(h.getHealth()-5);
		Random r = new Random();
		Grasslands t = findNear(g1,g2,h);
		if (c_counter == 0)  // case when no carnivorus
		{
			int p = r.nextInt()*100;
			if (p<=50)   // the case of not doing anything
			{
				if (inside!=null)  
				{
					h.counter = 0;
					int val = distance(h.getxCoordinate(),h.getyCoordinate(),inside.getX(),inside.getY());
					if (inside.getQuantity()>= h.getCapacity())
						{
							inside.setQuantity(inside.getQuantity()-h.getCapacity());
							h.setHealth(h.getHealth()+h.getHealth()/2);  // 50%
						}
					else 
					{
						inside.setQuantity(0);
						h.setHealth(h.getHealth()+h.getHealth()/5);   // 20%
					}
				}
			}
			else
			{
				h.counter++;
				if (inside == null)
				{
					Grasslands temp = findNear(g1,g2,h);
					// move x directions further
					h.shift(5,temp.getX(),temp.getY());
				}
					
			}
			return h;
		}
		Animal a = h.nearAnimal(animals[2], animals[3]);
		if (inside == null)   // if not inside grassland
		{
			h.counter++;
			int p = r.nextInt()*100;
			if (p >= 5)
			{
			p = r.nextInt()*100;
			if (p<=65)
			{
				h.shift(5,t.getX(),t.getY());
				//move 5 units towards nearest grassland
			}
			else
			{
				h.shift(-4, a.getxCoordinate(),a.getyCoordinate());
				// move 4 units away from nearest carnivore
			}
			}	
		}
		else
		{
			h.counter = 0;
		if (h.getCapacity()<=inside.getQuantity()) // herbivore has enough to eat
		{
			inside.setQuantity(inside.getQuantity()-h.getCapacity());
			int p = r.nextInt()*100;
			if (p<=90)
			{
				h.setHealth(h.getHealth()+h.getHealth()/2); 
			}
			else
			{
				h.setHealth(h.getHealth()-25); 
				if (h.getHealth()<=0)
					return null;
				 p = r.nextInt()*100;
				if (p<=50)
				{
					h.shift(-2, a.getxCoordinate(),a.getyCoordinate());
					// go away 2 units from nearest carnivore
				}
				else
				{
					h.shift(3,t.getX(),t.getY());
					// go 3 units towards next grassland
				}
			}
		}
		else  // doesn't have enough capacity
		{
			int	p = r.nextInt()*100;
			if(p<=20)
			{
				inside.setQuantity(0);
				h.setHealth(h.getHealth()+h.getHealth()/5); 
				// stay here and eat
			}
			else
			{
				p = r.nextInt()*100;
				if (p<=70)
				{
					h.shift(-4,a.getxCoordinate(),a.getyCoordinate());
					//4 units away from carnivore
				}
				else
				{
					h.shift(2,t.getX(),t.getY());
					// 2 units towards next grassland
				}
			}
		}
		}
		
		return h;
	}
	
	public Grasslands findNear(Grasslands g1,Grasslands g2,Animal h)
	{
		int d1 = distance(h.getxCoordinate(),h.getyCoordinate(),g1.getX(),g1.getY());
		int d2 =  distance(h.getxCoordinate(),h.getyCoordinate(),g2.getX(),g2.getY());
		if (d1 < d2)
			return g1;
		else 
			return g2;
	}
}

class Carnivorus extends Animal
{
	Carnivorus(String name,int t,int h,int x,int y,int e)
	{
		super(t,h,x,y, e,name);
	}
	public Animal takeTurn(Animal h, int h_counter,Grasslands inside,Grasslands g1,Grasslands g2,Animal animals[],PriorityQueue<Animal> q)
	{
		if (h.counter>7)
			h.setHealth(h.getHealth()-6);
		Random r = new Random();
		if (h_counter == 0)
		{
			return h;
		}
		Animal a = h.nearAnimal(animals[0],animals[1]);
		int dis = h.distance(h.getxCoordinate(),h.getyCoordinate(),a.getxCoordinate(),a.getyCoordinate());
		if (dis>=5)
			h.counter++;
		else
			h.counter = 0;
		if (dis<=1)
		{
			q.remove(a);
			h.setHealth(h.getHealth()+2*a.getHealth()/3);
			// eat herbivore
			// return heree
			return h;
		}
		if (inside == null)
		{
			int p = r.nextInt()*100;
			if (p<=92)
			{
				h.shift(4,a.getxCoordinate(),a.getyCoordinate());
				// 4 units towards herbivore
				// if herbivore near eat it;
			}
			else
			{
				h.setHealth(h.getHealth()-60);
				// stay
			}
		}
		else
		{
			int p = r.nextInt()*100;
			if (p<=25)
			{
				h.setHealth(h.getHealth()-30);
				// stay there
			}
			else
			{
				h.shift(2,a.getxCoordinate(),a.getyCoordinate());
				// 2 units towards nearest herbivore
			}
		}
		return h;
	}
}

class Grasslands
{
	int x;
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	int y;
	int radius;
	int quantity;
	Grasslands(int x,int y,int r,int q)
	{
		this.x = x;
		this.y = y;
		radius = r;
		quantity = q;
	}
}
class World{
	static int clock;
	Animal[] animals = new Animal[4];
	Grasslands gl1,gl2;
	int c_counter = 2,h_counter = 2;
	static Scanner scan = new Scanner(System.in);
	void Simulate()
	{
	//	System.out.println("Enter total time for Simulation");
		clock = scan.nextInt();
		takeinput();
		PriorityQueue<Animal> queue = new PriorityQueue<Animal>(new compareAnimal());
		for (int i = 3;i >=0;i--)
			queue.add(animals[i]);
		System.out.println(queue.toString());
		int turns = 0;
		while(!queue.isEmpty()  && turns!=clock)
		{
			Animal a = queue.poll();
			Grasslands inside = isInside(a,gl1,gl2);
			Animal x =a.takeTurn(a,c_counter,inside,gl1,gl2,animals,queue);
			if (x != null)
				{
				if (x.getHealth()<=0)
				{
					System.out.println("it is "+x.getName()+"\n it is dead ");
					continue;
				}
				Random r = new Random();
				int p = r.nextInt()*clock+turns;
				x.setTimestamp(p);
					queue.add(x);
				if (x.getName().equals("First Herbivore")|| x.getName().equals("Second Herbivore"))
					{
						System.out.println("this is "+x.getName());
						System.out.println("health is "+x.getHealth());
						h_counter--;
					}
				else
				{
					System.out.println("this is "+x.getName());
					System.out.println("health is "+x.getHealth());
					c_counter--;
				}	
				}
			else   // if x is null
			{
				System.out.println("it is "+a.getName()+"\n it is dead ");
			}
			turns++;
		}
	}
	
	public Grasslands isInside(Animal a,Grasslands gl1,Grasslands gl2) // checks if animal inside grassland
	{
		int x1 = a.getxCoordinate();
		int y1 = a.getyCoordinate();
		int x2 = gl1.getX();
		int y2 = gl1.getY();
		double m = Math.pow(x2-x1,2);
		double n = Math.pow(y2-y1,2);
		if (Math.pow(m+n,0.5) <= gl1.getRadius()*gl1.getRadius())
			return gl1;
		 x2 = gl2.getX();
		 y2 = gl2.getY();
		 if (Math.pow(m+n,0.5) <= gl2.getRadius()*gl2.getRadius())
			 return gl2;
		return null;
	}
	
	void takeinput()
	{
		String[] messageArray = new String[7];
		messageArray[0] = "Enter x,y,radius and GrassAvailable for First GrassLand";
		messageArray[1] = "Enter x,y,radius and GrassAvailable for Second GrassLand";
		messageArray[2] = "Enter Health and Capacity for Herbivorus";
		messageArray[3] = "Enter x,y position and timestamp for first herbivorus";
		messageArray[3] = "Enter x,y position and timestamp for first herbivorus";
		int a,b,c,d,e;
		System.out.println("Enter x,y,radius and GrassAvailable for First GrassLand	");
		a = scan.nextInt();
		b = scan.nextInt();
		c = scan.nextInt();
		d = scan.nextInt();
		gl1 = new Grasslands(a,b,c,d);
		System.out.println("Enter x,y,radius and GrassAvailable for Second GrassLand	");
		a = scan.nextInt();
		b = scan.nextInt();
		c = scan.nextInt();
		d = scan.nextInt();
		gl2 = new Grasslands(a,b,c,d);
		System.out.println("Enter Health and Capacity for Herbivorus");
		a = scan.nextInt();
		b = scan.nextInt();
		System.out.println("Enter x,y position and timestamp for first herbivorus");
		c = scan.nextInt();
		d = scan.nextInt();
		e = scan.nextInt();
		animals[0] = new Herbivorus("First Herbivore",e,a,b,c,d);
		System.out.println("Enter x,y position and timestamp for first herbivorus");
		c = scan.nextInt();
		d = scan.nextInt();
		e = scan.nextInt();
		animals[1] = new Herbivorus("Second Herbivore",e,a,b,c,d);
		System.out.println("Enter Health for Carnivorus");
		a = scan.nextInt();
		System.out.println("Enter x,y position and timestamp for first Carnivorus");
		b = scan.nextInt();
		c = scan.nextInt();
		d = scan.nextInt();
		animals[2] = new Carnivorus("First Carnivore",d,a,b,c,0);
		System.out.println("Enter x,y position and timestamp for Second Carnivorus");
		b = scan.nextInt();
		c = scan.nextInt();
		d = scan.nextInt();	
		animals[3] = new Carnivorus("Second Carnivore",d,a,b,c,0);
		
	}
}

class compareAnimal implements Comparator<Animal>
{
	@Override
	public int compare(Animal o1, Animal o2) {
		if (o1.getTimestamp() == o2.getTimestamp())
		{
			if (o1.getHealth() == o2.getHealth())
			{
				double val1 = Math.pow(o1.getxCoordinate()*o1.getxCoordinate()+o1.getyCoordinate()*o1.getyCoordinate(), 0.5);
				double val2 = Math.pow(o2.getxCoordinate()*o2.getxCoordinate()+o2.getyCoordinate()*o2.getyCoordinate(), 0.5);
				return (int)-(val2-val1);
			}
			else
				return -(o1.getHealth()-o2.getHealth());
		}
		else
			return -(o2.getTimestamp()-o1.getTimestamp());
	}

}
 
public class Main {

	public static void main(String[] args) {
		World world = new World();
		world.Simulate();
		

	}
	

}
/*
 25
 0 5 4 15
 0 0 1 25
 15 20
 2 2 5
 0 -5 15
 25
 1 5 12
 2 7 10
  
  
  
  
  
  
 * 
 * */
