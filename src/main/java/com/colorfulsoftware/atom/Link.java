/**
 * Copyright 2011 Bill Brown
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Change History:
 *  2006-11-14 wbrown - added javadoc documentation.
 *  2008-04-08 wbrown - added exception for unsupported attribute.
 *  2008-04-09 wbrown - added throws clause to constructor and check for non supported attribute.
 */
package com.colorfulsoftware.atom;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents an Atom 1.0 link element.
 * 
 * @see <a
 *      href="http://www.atomenabled.org/developers/syndication/atom-format-spec.php">Atom
 *      Syndication Format</a>
 * @author Bill Brown
 * 
 *         <pre>
 * 	    atomLink =
 *          element atom:link {
 *          atomCommonAttributes,
 *          attribute href { atomUri },
 *          attribute rel { atomNCName | atomUri }?,
 *          attribute type { atomMediaType }?,
 *          attribute hreflang { atomLanguageTag }?,
 *          attribute title { text }?,
 *          attribute length { text }?,
 *          undefinedContent
 *         }
 * </pre>
 */
public class Link implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7730515433344653457L;
	private final List<Attribute> attributes;
	private final Attribute href;
	private final Attribute rel;
	private final Attribute type;
	private final Attribute hreflang;
	private final Attribute title;
	private final Attribute length;
	private final String content;
	private List<String> unboundPrefixes = null;

	// use the factory method in the FeedDoc.
	Link(List<Attribute> attributes, String content) throws AtomSpecException {

		this.unboundPrefixes = new LinkedList<String>();

		if (attributes == null) {
			throw new AtomSpecException(
					"atom:link elements MUST have an href attribute, whose value MUST be a IRI reference.");
		} else {
			this.attributes = new LinkedList<Attribute>();
			for (Attribute attr : attributes) {
				// check for unsupported attribute.
				if (!new AttributeSupport(attr).verify(this)) {
					throw new AtomSpecException("Unsupported attribute "
							+ attr.getName() + " for this link element.");
				}
				this.attributes.add(new Attribute(attr));
				// check for unbound attribute prefixes
				if (attr.getName().indexOf(":") != -1
						&& !attr.getName().equals("xml:lang")
						&& !attr.getName().equals("xml:base")
						&& !attr.getName().startsWith("xmlns:")
						&& getAttribute("xmlns:"
								+ attr.getName().substring(0,
										attr.getName().indexOf(":"))) == null) {
					this.unboundPrefixes.add(attr.getName().substring(0,
							attr.getName().indexOf(":")));
				}
			}
		}

		this.unboundPrefixes = (this.unboundPrefixes.size() == 0) ? null
				: this.unboundPrefixes;

		if ((this.href = getAttribute("href")) == null) {
			throw new AtomSpecException(
					"atom:link elements MUST have an href attribute, whose value MUST be a IRI reference.");
		}
		if (this.href.getValue() == null || this.href.getValue().equals("")) {
			throw new AtomSpecException(
					"Link href attribue SHOULD NOT be blank.");
		}

		this.rel = getAttribute("rel");

		this.type = getAttribute("type");

		this.hreflang = getAttribute("hreflang");

		this.title = getAttribute("title");

		this.length = getAttribute("length");

		this.content = (content == null || content.equals("")) ? null : content;
	}

	Link(Link link) {
		this.attributes = link.getAttributes();
		this.href = link.getHref();
		this.rel = link.getRel();
		this.type = link.getType();
		this.hreflang = link.getHreflang();
		this.title = link.getTitle();
		this.length = link.getLength();
		this.content = link.content;
	}

	/**
	 * 
	 * @return the category attribute list.
	 */
	public List<Attribute> getAttributes() {

		List<Attribute> attrsCopy = new LinkedList<Attribute>();
		for (Attribute attr : this.attributes) {
			attrsCopy.add(new Attribute(attr));
		}
		return attrsCopy;
	}

	/**
	 * 
	 * @return the href contains the link's IRI
	 */
	public Attribute getHref() {
		return new Attribute(href);
	}

	/**
	 * 
	 * @return the hreflang describes the language of the resource pointed to by
	 *         the href attribute.
	 */
	public Attribute getHreflang() {
		return (hreflang == null) ? null : new Attribute(hreflang);
	}

	/**
	 * 
	 * @return the length indicates an advisory length of the linked content in
	 *         octets.
	 */
	public Attribute getLength() {
		return (length == null) ? null : new Attribute(length);
	}

	/**
	 * 
	 * @return the rel which matches either the "isegment-nz-nc" or the "IRI"
	 *         production in [RFC3987]
	 */
	public Attribute getRel() {
		return (rel == null) ? null : new Attribute(rel);
	}

	/**
	 * 
	 * @return the title conveys human-readable information about the link.
	 */
	public Attribute getTitle() {
		return (title == null) ? null : new Attribute(title);
	}

	/**
	 * 
	 * @return the type which is an advisory media type.
	 */
	public Attribute getType() {
		return (type == null) ? null : new Attribute(type);
	}

	/**
	 * 
	 * @return undefined text content or undefined element.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param attrName
	 *            the name of the attribute to get.
	 * @return the Attribute object if attrName matches or null if not found.
	 */
	public Attribute getAttribute(String attrName) {
		for (Attribute attribute : this.attributes) {
			if (attribute.getName().equals(attrName)) {
				return new Attribute(attribute);
			}
		}
		return null;
	}

	/**
	 * Shows the contents of the &lt;link> element.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<link");
		for (Attribute attribute : attributes) {
			sb.append(attribute);
		}

		if (content == null) {
			sb.append(" />");
		} else {
			sb.append(" >" + content + "</link>");
		}

		return sb.toString();
	}

	List<String> getUnboundPrefixes() {
		return unboundPrefixes;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Link)) {
			return false;
		}
		return this.toString().equals(obj.toString());
	}
	
	@Override public int hashCode() {
		return toString().hashCode();
	}
}
