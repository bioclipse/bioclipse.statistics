package net.bioclipse.r.ui.testers;

import net.bioclipse.r.business.Activator;

import org.eclipse.core.expressions.PropertyTester;

/**
 * PropertyTester class that checks if R is working
 *
 * @author valyo
 *
 */
public class RTester extends PropertyTester{


	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if ("working".equalsIgnoreCase(property)){
			
			if (!(expectedValue instanceof Boolean)) return false;
			
			boolean expected=(Boolean)expectedValue;
			boolean actual = Activator.getDefault().getJavaRBusinessManager().isWorking();
			
    		return (actual==expected);
		}
		return false;
	}

}
