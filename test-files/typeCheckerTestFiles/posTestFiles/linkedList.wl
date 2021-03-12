class LinkedList {
	head : Node <- new Node ; #null head
	
	(*Adds an object to the tail of the linked list *)
	add(d: Object): Object
	{
		tail: Node <- getTail() ;
		newNode: Node <- new Node ;
		{
			newNode.setData(d);
			tail.setNext(newNode);
			d;
		}
	}
	
	(*Gets the value of the object at a particular location in the linked list 
	or null if it doesn't exist *)
	get(loc: int): Object
	{
		curPos: int <- 0;
		curNode: Node <- getTail().getNext(); 
		stillLoop: boolean <- true; #wool doesn't have and, so I do this instead
		{
			if loc < 0 then #just to make sure the function terminates and returns null if loc < 0
			{
				stillLoop <- false;
				curNode <- null; 
			}
			else null fi;
			while stillLoop
			loop 
				if isnull curNode then stillLoop <- false
				else if curPos >= loc then stillLoop <- false
					else{
						curNode <- curNode.getNext();
						curPos <- curPos + 1;
            true;
					} 
					fi
				fi
			pool;
			if isnull curNode then null
				else 
				{
					curNode.getData();
				}
			fi;
		}	
	}
	
	(*sets the value at location to d 
	location must be >= 0*)
	set(loc: int, d: Object): Object
		{
		curPos: int <- 0;
		curNode: Node <- getTail().getNext(); 
		stillLoop: boolean <- true; #wool doesn't have and, so I do this instead
		{
			if loc < 0 then
			{stillLoop <- false;
			curNode <- null; }
			else null fi;
			while stillLoop
			loop 
				if isnull curNode then stillLoop <- false
				else if curPos >= loc then stillLoop <- false
					else{
						curNode <- curNode.getNext();
						curPos <- curPos + 1;
            true;
					} 
					fi
				fi
			pool;
			if isnull curNode then null
				else 
				{
					curNode.setData(d);
				}
			fi;
		}	
	}
	
	
	(*removes the object at location loc from the linked list
	returns the value at that location or null if it doesn't exist*)
	pop(loc: int): Object
	{
		curPos: int <- 0;
		curNode: Node <- getTail().getNext();
		prevNode: Node <- getTail();
		stillLoop: boolean <- true; #wool doesn't have and, so I do this instead
		{
			if loc < 0 then
				{
				stillLoop <- false;
				curNode <- null; 
				}
			else null fi;
			while stillLoop
			loop 
				if isnull curNode then stillLoop <- false
				else if curPos >= loc then stillLoop <- false
					else{
						prevNode <- curNode;
						curNode <- curNode.getNext();
						curPos <- curPos + 1;
            true;
					} 
					fi
				fi
			pool;
			if isnull curNode then null
				else 
				{
					prevNode.setNext(curNode.getNext());
					curNode.getData();
				}
			fi;
		}	
	}
	
	(*Gets the tail from the linked list *)
	getTail(): Node
	{
		tail: Node <- head ;
		{
			while ~isnull tail.getNext()
			loop tail <- tail.getNext()
			pool; #once the tail's next is null, then it's the tail
			tail; #returns the tail
		}		
	}
	
	(*gets the size of the linked list*)
	size(): int
	{
		curNode: Node <- head ;
		curPos: int <- 0 ;
		{
			while ~isnull curNode.getNext()
			loop 
			{
				curNode <- curNode.getNext();
				curPos <- curPos + 1;
			}
			pool; #once the tail's next is null, then it's the tail
			curPos; #returns the position
		}
	}
	
	(*returns true if the linked list contains the object passed in and false otherwise
	Has to be an object at the same memory address*)
	
	contains(d: Object): boolean
	{
		curNode: Node <- getTail().getNext();
		stillLoop: boolean <- true; #wool doesn't have and, so I do this instead
		{
			while stillLoop
			loop 
				if isnull curNode then stillLoop <- false
				else if curNode.getData() = d then stillLoop <- false
					else{
						curNode <- curNode.getNext();
            true;
					} 
					fi
				fi
			pool;
			if isnull curNode then false
				else true
			fi;
		}	
	}
	
}

class Node 
{
	data: Object ;
	next: Node ;
	
	setData(d: Object): Object 
	{
		data <- d #should just return d
	}
	
	getData(): Object
	{
		data #Shouldn't need a semicolon since ID alone is an expression
	}
	
	setNext(n: Node): Node
	{
		next <- n
	}
	
	getNext(): Node
	{
		next
	}	
}