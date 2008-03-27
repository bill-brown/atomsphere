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
package com.colorful.atom;

import java.util.List;

/**
 * This class represents an Atom 1.0 logo element.
 * @see <a href="http://www.atomenabled.org/developers/syndication/atom-format-spec.php">Atom Syndication Format</a>
 * @author Bill Brown
 *	<pre>
 *      atomLogo = element atom:logo {
 *          atomCommonAttributes,
 * 		    (atomUri)
 *	        }
 *	</pre>
 */
public class Logo {

	private final AtomURIConstruct logo;

	//use the factory method in the FeedDoc.
	Logo(List<Attribute> attributes, String atomUri){
		this.logo = new AtomURIConstruct(attributes,atomUri);
	}

	/** 
	 * 
	 * @return the unique identifier for this document.
	 */
	public String getAtomUri(){
		return logo.getAtomUri();
	}

	/**
	 * 
	 * @return the attributes for this element.
	 */
	public List<Attribute> getAttributes(){
		return logo.getAttributes();
	}
}