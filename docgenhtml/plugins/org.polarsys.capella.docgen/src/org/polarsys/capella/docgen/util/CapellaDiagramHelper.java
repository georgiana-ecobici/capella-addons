/*******************************************************************************
 * Copyright (c) 2006, 2015 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *   Thales - initial API and implementation
 ******************************************************************************/
package org.polarsys.capella.docgen.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.polarsys.kitalpha.doc.gen.business.core.util.IDiagramHelper;

import org.polarsys.capella.core.data.cs.ExchangeItemAllocation;
import org.polarsys.capella.core.data.information.AbstractEventOperation;
import org.polarsys.capella.core.data.interaction.Execution;
import org.polarsys.capella.core.data.interaction.InstanceRole;
import org.polarsys.capella.core.data.interaction.InteractionState;
import org.polarsys.capella.core.data.interaction.InteractionUse;
import org.polarsys.capella.core.data.interaction.SequenceMessage;
import org.polarsys.capella.core.data.interaction.StateFragment;
import org.polarsys.capella.core.data.capellacore.NamedElement;

/**
 * 
 */
public class CapellaDiagramHelper implements IDiagramHelper {

	public boolean select(EObject eObject) {
		if (eObject instanceof NamedElement) 
		{
			return CapellaServices.isLinkableWithoutScope(eObject) >= 0;
//			return CapellaServices.isElementLinkable(eObject);
		} 
		else 
		{
			if (eObject instanceof SequenceMessage) 
			{
				AbstractEventOperation eventOperation = ((SequenceMessage) eObject).getInvokedOperation();
				if (eventOperation != null)
				{
					return CapellaServices.isLinkableWithoutScope(eventOperation) >= 0;
//					return CapellaServices.isElementLinkable(eventOperation);
				}
			}
		}

		return false;
	}

	public EObject getSemanticElement(DDiagramElement element) {
		EObject target = element.getTarget();
		if (target instanceof SequenceMessage) {
			return ((SequenceMessage) target).getInvokedOperation();
		} else {
			if (target instanceof InstanceRole) {
				return ((InstanceRole) target).getRepresentedInstance();
			} else {
				if (target instanceof InteractionState) {
					if (((InteractionState) target).getRelatedAbstractFunction() != null)
						return ((InteractionState) target).getRelatedAbstractFunction();

					return ((InteractionState) target).getRelatedAbstractState();
				} else {
					if (target instanceof InteractionUse) {
						return ((InteractionUse) target).getReferencedScenario();
					} else {
						if (target instanceof Execution)
							return null;

						if (target instanceof StateFragment)
							return ((StateFragment) target).getRelatedAbstractFunction();
					}
				}
			}
		}
		return target;
	}

	public boolean isContainer(DDiagramElement element) {
		EObject semantic = element.getTarget();
		if (semantic instanceof Execution || semantic instanceof InstanceRole)
			return false;

		return true;
	}

	public String getElementId(EObject eObject) {
		EObject element = eObject;
		if (eObject instanceof ExchangeItemAllocation)
			element = ((ExchangeItemAllocation) eObject).getAllocatedItem();

		if (element != null)
			return CapellaServices.getAnchorId(element);
		else
			return "";
	}

	public String diagramDocumentationPostTraitement(EObject eObject, String documentation, String projectName, String outputFolder) {
		return StringUtil.transformAREFString(eObject, documentation, projectName, outputFolder);
	}

}
