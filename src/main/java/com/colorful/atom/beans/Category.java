/*
Atomsphere - an atom feed library.
Copyright (C) 2006 William R. Brown.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/* Change History:
 *  2006-11-14 wbrown - added javadoc documentation.
 */
package com.colorful.atom.beans;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents an Atom 1.0 category.
 * @see <a href="http://www.atomenabled.org/developers/syndication/atom-format-spec.php">Atom Syndication Format</a>
 * @author Bill Brown
 *  <pre>
 *      atomCategory =
 *          element atom:category {
 *          atomCommonAttributes,
 *          attribute term { text },
 *          attribute scheme { atomUri }?,
 *          attribute label { text }?,
 *          undefinedContent
 *  </pre>
 */
public class Category {

    
    private final List<Attribute> attributes;
    private final Attribute term; //required
    private final Attribute scheme;
    private final Attribute label;
    
    /**
     * 
     * @param term identifies the category of the feed.
     * @param scheme identifies a categorization scheme.
     * @param label provides a human-readable label for display in end-user applications.
     * @param attributes additional attributes for this element which may or may not contain any of the first three 
     */
    public Category(Attribute term, Attribute scheme, Attribute label, List<Attribute> attributes){
    	
    	this.term = (term == null)?null:new Attribute(term.getName(),term.getValue());
    	this.scheme = (scheme == null)?null:new Attribute(scheme.getName(),scheme.getValue());
    	this.label = (label == null)?null:new Attribute(label.getName(),label.getValue());
    	
    	if(term == null && scheme == null && label == null && attributes == null){
    		this.attributes = null;
    		return;
    	}
        
        if(attributes == null){
        	this.attributes = new LinkedList<Attribute>();
    	}else{
    		this.attributes = new LinkedList<Attribute>();
    		Iterator<Attribute> attrItr = attributes.iterator();
    		while(attrItr.hasNext()){
    			Attribute attr = attrItr.next();
    			this.attributes.add(new Attribute(attr.getName(),attr.getValue()));
    		}
    	}
        
        if(term != null){
        	attributes.add(this.term);
        }
        
        if(scheme != null){
        	attributes.add(this.scheme);
        }
        
        if(label != null){
        	attributes.add(this.label);
        }
    }
    
    /**
     * 
     * @return the category attribute list.
     */
    public List<Attribute> getAttributes() {
    	List<Attribute> attrsCopy = new LinkedList<Attribute>();
		Iterator<Attribute> attrItr = this.attributes.iterator();
		while(attrItr.hasNext()){
			Attribute attr = attrItr.next();
			attrsCopy.add(new Attribute(attr.getName(),attr.getValue()));
		}
        return attrsCopy;
    }
    
    /**
     * 
     * @return the label attribute
     */
    public Attribute getLabel() {
    	return (label == null)?null:new Attribute(label.getName(),label.getValue());
    }

    /**
     * 
     * @return the scheme attribute
     */
    public Attribute getScheme() {
    	return (scheme == null)?null:new Attribute(scheme.getName(),scheme.getValue());
    }

    /**
     * 
     * @return the term attribute
     */
    public Attribute getTerm() {
    	return (term == null)?null:new Attribute(term.getName(),term.getValue());
    }

}
