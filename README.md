## Description
A prototype Java server to schedule delivery orders.

## Running the server
To build the project, [**Java 8**](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and [**maven**](https://maven.apache.org/install.html) are required

    $ git clone https://github.com/lawkaringithi/order-delivery-server.git
    $ cd order-delivery-server
    $ mvn package
    
Starting the server;
    
    $ java -jar target/order-server-1.0-jar-with-dependencies.jar
    $ ...
    $ ...
    
You should see a few logs and a message to show the server is now running
    
    $ ...
    $ ...
    $ Server running on port 8080

If the **PORT** system variable has not been set, the server defaults to port 8080. Otherwise, the server starts on the specified port.

## Making a delivery order
Assuming the server is running on port 8080, new orders can be submitted via the **/detour** end point.

New order can be assigned to people who have already been assigned other orders if;

1. The difference between the origins of the new order and an already existing order is 1.5 Km or less
2. The difference between the destinations of the new order and the order that passes **(1)** above is 1.5 Km or less
3. The time difference between the the new order and the order that passes **(1)** and **(2)** is 1 minute or less

For example, a delivery order from **Nebraska, USA** to **Kansas, USA** is assigned to a relevant person and returns the **detour** (distance between the origin and destination) and **detour type** (the algorithm used to calculate the distance).

    http://localhost:8080/detour?origin=41.507483,-99.436554&destination=38.504048,-98.315949
    
    {
	  data: {
	    detour: "347.3283480394256",
	    type: "default"
	  }
	}
	
	
## Querying orders
Order querying can be done through **/orders** which returns all orders and to whom they have been assigned to (The **id** representing the assignee identity).

    http://localhost:8080/orders
    
    {
	  data: [
	    {
	      orders: [
	        {
	          from: "41.507483,-99.436554",
	          to: "38.504048,-98.315949"
	        }
	      ],
	      id: 1
	    }
	  ]
	}
	

## Test
If an order is made from **iHub, Nairobi** (-1.2987187, 36.788576) to **Strathmore University, Madaraka** (-1.3089589, 36.8108885)

**/orders** returns
    
    {
	  data: [
	    {
	      orders: [
	        {
	          from: "-1.2987187, 36.788576",
	          to: "-1.3089589, 36.8108885"
	        }
	      ],
	      id: 1
	    }
	  ]
	}
	
If another delivery order is made from **Nebraska, USA** (41.507483, -99.436554) to **Kansas, USA** (38.504048, -98.315949) **10 seconds later**

**/orders** returns

    {
	  data: [
	    {
	      orders: [
	        {
	          from: "-1.2987187, 36.788576",
	          to: "-1.3089589, 36.8108885"
	        }
	      ],
	      id: 1
	    },
	    {
	      orders: [
	        {
	          from: "41.507483,-99.436554",
	          to: "38.504048,-98.315949"
	        }
	      ],
	      id: 2
	    }
	  ]
	}
	
	// The new order is assigned to a new person since it doesn't
	// meet the criteria to be assigned to an existing person
	
	
If a delivery order is made from **Kilimall, Ngong road** (-1.2993223, 36.7864966) to **T-Mall, Lang'ata road** (-1.3121364, 36.8145944) **15 seconds after the second order**

**/orders** returns

    {
	  data: [
	    {
	      orders: [
	        {
	          from: "-1.2987187, 36.788576",
	          to: "-1.3089589, 36.8108885"
	        },
	        {
	          from: "-1.2993223, 36.7864966",
	          to: "-1.3121364, 36.8145944"
	        }
	      ],
	      id: 1
	    },
	    {
	      orders: [
	        {
	          from: "41.507483,-99.436554",
	          to: "38.504048,-98.315949"
	        }
	      ],
	      id: 2
	    }
	  ]
	}
	
	// The new order is assigned to person with id (1) since it
	// meets the criteria for the person to handle it
