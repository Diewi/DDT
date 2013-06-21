package dtool.ast.references;

import dtool.refmodel.DefUnitDescriptor;

// TODO: review this hierarchy
public abstract class CommonRefNative extends Reference {
	
	@Override
	public boolean canMatch(DefUnitDescriptor defunit) {
		if(defunit.isNative())
			return true;
		return false;
	}
	
}
