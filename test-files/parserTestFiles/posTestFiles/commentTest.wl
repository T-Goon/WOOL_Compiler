class List { (* empty class*)}
class Cons inherits List {
	# )[( this false true $ !
	(* )[( this false true $ ! *)
	xcar : int (* comment right before semicolon*) ;
	xcdr : List;
	
	# this is the isNil function
	isNil() : Bool { false }
	
	init(hd : int, tl : (* strange place to put a comment *) List) : Cons { # comment
		{
			xcar <- hd; # this should work
			xcdr <- tl;
			
			(* first *)
			(* (* first *) middle *)
			(* middle (* last *) *)
			(* (* first *) middle (* last *) (* last again *) *)
			
			this;
		} # it would be quite upsetting if this doesn't work
	}	# this should also work
}