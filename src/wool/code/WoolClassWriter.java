package wool.code;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import wool.symbol.AbstractBinding;
import wool.symbol.BindingFactory.ClassBinding;
import wool.symbol.BindingFactory.ObjectBinding;
import wool.symbol.TableManager;
import wool.utility.WoolException;

public class WoolClassWriter extends ClassWriter{
	
	private ObjectBinding boolBinding;
	private ObjectBinding intBinding;
	private TableManager tm;

	public WoolClassWriter(int flags) {
		super(flags);
		
		tm = TableManager.getInstance();
		// Binding for boolean constants
		boolBinding = new ObjectBinding("boolean", "Bool", null);
		// Binding for int constants
		intBinding = new ObjectBinding("int", "Int", null);
	}

	@Override
	protected String getCommonSuperClass(String arg0, String arg1) {
		
		// Only works for wool classes
		if(!arg0.substring(0,4).equals("wool")) {
			return super.getCommonSuperClass(arg0, arg1);
		}
		
		
		if(arg0.equals(boolBinding.symbolType)||
				arg0.equals(intBinding.symbolType)||
				arg1.equals(boolBinding.symbolType)||
				arg1.equals(intBinding.symbolType)) {
			
			// Both not type int or bool
			if(!arg0.equals(arg1)) {
				return null;
			}
			
		}
		
		// Do join operation and make sure it is not null
		String join;
		ClassBinding t = tm.lookupClass(arg0.substring(5));
		// Look at all parents of then and check to see if els ever conforms to one
		while(t != null) {
			join = conforms(t.symbolType, arg1.substring(5));
			
			if(join != null) {
				
				return "wool/"+join;
			}

			t = tm.lookupClass(t.getClassDescriptor().inherits);
		}
		
		return null;
	}

	/**
	 * Checks is the second type conforms to the first type.
	 * @param abType String that contains the first type
	 * @param abExpr String that contains the second type
	 * @return abType if abExpr does conform and null otherwise
	 */
	private String conforms(String abType, String abExpr) {
		ClassBinding cb = tm.lookupClass(abExpr);
		ClassBinding parent;
		
		// Check expression type conforms to declared type
		do {
			parent = tm.lookupClass(cb.getClassDescriptor().inherits);
			
			if(abType.equals(abExpr)) {
				return abType;
			}
			
			cb = parent;
			if(cb == null) {
				return null;
			}
			abExpr = cb.symbolType;
		}while(abExpr != null);
		
		return null;
	}

}
