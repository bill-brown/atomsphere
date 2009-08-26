/**
 * Copyright (C) 2009 William R. Brown <info@colorfulsoftware.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/* Change History:
 *  2006-11-14 wbrown - added javadoc documentation.
 *  2008-03-16 wbrown - made class immutable.
 */
package com.colorful.atom;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents an Atom 1.0 entry element.
 * 
 * @see <a
 *      href="http://www.atomenabled.org/developers/syndication/atom-format-spec.php">Atom
 *      Syndication Format</a>
 * @author Bill Brown
 * 
 *         <pre>
 *      atomEntry =
 *          element atom:entry {
 *          atomCommonAttributes,
 *          (atomAuthor*
 *          &amp; atomCategory*
 *          &amp; atomContent?
 *          &amp; atomContributor*
 *          &amp; atomId
 *          &amp; atomLink*
 *          &amp; atomPublished?
 *          &amp; atomRights?
 *          &amp; atomSource?
 *          &amp; atomSummary?
 *          &amp; atomTitle
 *          &amp; atomUpdated
 *          &amp; extensionElement*)
 *          }
 * </pre>
 */
public class Entry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5291388675692200218L;
	private final AtomEntrySourceAdaptor entryAdaptor;
	private final Content content;
	private final Published published;
	private final Source source;
	private final Summary summary;

	// use the factory method in the FeedDoc.
	Entry(Id id, Title title, Updated updated, Rights rights, Content content,
			List<Author> authors, List<Category> categories,
			List<Contributor> contributors, List<Link> links,
			List<Attribute> attributes, List<Extension> extensions,
			Published published, Summary summary, Source source)
			throws AtomSpecException {

		this.entryAdaptor = new AtomEntrySourceAdaptor(id, title, updated,
				rights, authors, categories, contributors, links, attributes,
				extensions);

		// check for functional requirements here because
		// they are all optional for a Source element.

		// make sure id is present
		if (id == null) {
			throw new AtomSpecException(
					"atom:entry elements MUST contain exactly one atom:id element.");
		}
		// make sure title is present
		if (title == null) {
			throw new AtomSpecException(
					"atom:entry elements MUST contain exactly one atom:title element.");
		}
		// make sure updated is present
		//it is actually checked further up in the reader because of how 
		//we store entries.

		if (content == null) {
			this.content = null;
		} else {

			if (content.getAttributes() != null) {
				for (Attribute attr : content.getAttributes()) {
					// check for src attribute
					if (attr.getName().equals("src") && summary == null) {
						throw new AtomSpecException(
								"atom:entry elements MUST contain an atom:summary element if the atom:entry contains an atom:content that has a \"src\" attribute (and is thus empty).");
					}
					// check for non text or html or xhtml or xml mime types.
					if (attr.getName().equals("type")
							&& !attr.getValue().equals("text")
							&& !attr.getValue().equals("html")
							&& !attr.getValue().equals("xhtml")
							&& !attr.getValue().startsWith("text/")
							&& !attr.getValue().endsWith("/xml")
							&& !attr.getValue().endsWith("\\+xml")
							&& summary == null) {
						throw new AtomSpecException(
								"atom:entry elements MUST contain an atom:summary element if the atom:entry contains content that is encoded in Base64; i.e., the \"type\" attribute of atom:content is a MIME media type [MIMEREG], but is not an XML media type [RFC3023], does not begin with \"text/\", and does not end with \"/xml\" or \"+xml\".");
					}
				}
			}

			this.content = new Content(content);
		}

		this.published = (published == null) ? null : new Published(published
				.getDateTime(), published.getAttributes());
		this.source = (source == null) ? null : new Source(source.getId(),
				source.getTitle(), source.getUpdated(), source.getRights(),
				source.getAuthors(), source.getCategories(), source
						.getContributors(), source.getLinks(), source
						.getAttributes(), source.getExtensions(), source
						.getGenerator(), source.getSubtitle(),
				source.getIcon(), source.getLogo());
		this.summary = (summary == null) ? null : new Summary(summary);
	}

	/**
	 * 
	 * @return the content for this entry.
	 */
	public Content getContent() {
		try {
			return (content == null) ? null : new Content(content);
		} catch (Exception e) {
			// we should never get here.
			return null;
		}
	}

	/**
	 * 
	 * @return the published date for this entry.
	 */
	public Published getPublished() {
		try {
			return (published == null) ? null : new Published(published
					.getDateTime(), published.getAttributes());
		} catch (Exception e) {
			// we should never get here.
			return null;
		}
	}

	/**
	 * 
	 * @return the source for this element.
	 */
	public Source getSource() {
		try {
			return (source == null) ? null : new Source(source.getId(), source
					.getTitle(), source.getUpdated(), source.getRights(),
					source.getAuthors(), source.getCategories(), source
							.getContributors(), source.getLinks(), source
							.getAttributes(), source.getExtensions(), source
							.getGenerator(), source.getSubtitle(), source
							.getIcon(), source.getLogo());
		} catch (Exception e) {
			// this should never happen because
			// we check for errors on initial creation
			// but if it does, print the stack trace
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @return the summary for this element.
	 */
	public Summary getSummary() {
		try {
			return (summary == null) ? null : new Summary(summary);
		} catch (Exception e) {
			// we should never get here.
			return null;
		}
	}

	/**
	 * 
	 * @return the unique identifier for this entry.
	 */
	public Id getId() {
		return entryAdaptor.getId();
	}

	/**
	 * 
	 * @return the title for this element.
	 */
	public Title getTitle() {
		return entryAdaptor.getTitle();
	}

	/**
	 * 
	 * @return the updated date for this element.
	 */
	public Updated getUpdated() {
		return entryAdaptor.getUpdated();
	}

	/**
	 * 
	 * @return the associated rights for this entry.
	 */
	public Rights getRights() {
		return entryAdaptor.getRights();
	}

	/**
	 * 
	 * @return the authors for this entry.
	 */
	public List<Author> getAuthors() {
		return entryAdaptor.getAuthors();
	}

	/**
	 * 
	 * @return the categories for this element.
	 */
	public List<Category> getCategories() {
		return entryAdaptor.getCategories();
	}

	/**
	 * 
	 * @return the contributors for this entry.
	 */
	public List<Contributor> getContributors() {
		return entryAdaptor.getContributors();
	}

	/**
	 * 
	 * @return the links for this entry.
	 */
	public List<Link> getLinks() {
		return entryAdaptor.getLinks();
	}

	/**
	 * 
	 * @return the category attribute list.
	 */
	public List<Attribute> getAttributes() {
		return entryAdaptor.getAttributes();
	}

	/**
	 * 
	 * @return the extensions for this entry.
	 */
	public List<Extension> getExtensions() {
		return entryAdaptor.getExtensions();
	}
	
	public Attribute getAttribute(String attrName) {
		return entryAdaptor.getAttribute(attrName);
	}

	public Author getAuthor(String name) throws AtomSpecException {
		return entryAdaptor.getAuthor(name);
	}

	public Category getCategory(String termValue) throws AtomSpecException {
		return entryAdaptor.getCategory(termValue);
	}

	public Contributor getContributor(String name) throws AtomSpecException {
		return entryAdaptor.getContributor(name);
	}

	public Link getLink(String hrefVal) throws AtomSpecException {
		return entryAdaptor.getLink(hrefVal);
	}

	public Extension getExtension(String extName) {
		return entryAdaptor.getExtension(extName);
	}
}
