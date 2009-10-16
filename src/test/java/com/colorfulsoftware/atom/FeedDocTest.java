/**
 * Copyright (C) 2009 William R. Brown <wbrown@colorfulsoftware.com>
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
package com.colorfulsoftware.atom;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.colorfulsoftware.atom.AtomSpecException;
import com.colorfulsoftware.atom.Attribute;
import com.colorfulsoftware.atom.Author;
import com.colorfulsoftware.atom.Category;
import com.colorfulsoftware.atom.Contributor;
import com.colorfulsoftware.atom.Entry;
import com.colorfulsoftware.atom.Extension;
import com.colorfulsoftware.atom.Feed;
import com.colorfulsoftware.atom.FeedDoc;
import com.colorfulsoftware.atom.FeedReader;
import com.colorfulsoftware.atom.FeedWriter;
import com.colorfulsoftware.atom.Generator;
import com.colorfulsoftware.atom.Icon;
import com.colorfulsoftware.atom.Id;
import com.colorfulsoftware.atom.Link;
import com.colorfulsoftware.atom.Logo;
import com.colorfulsoftware.atom.Name;
import com.colorfulsoftware.atom.Rights;
import com.colorfulsoftware.atom.Title;
import com.colorfulsoftware.atom.Updated;

//uncomment stax-utils dependency in the root pom.xml to see exapmle usage.
//import javanet.staxutils.IndentingXMLStreamWriter;

/**
 * This class tests the feed library. See the source code for examples.
 * 
 * @author Bill Brown
 * 
 */
public class FeedDocTest implements Serializable {

	private static final long serialVersionUID = 4141631875438242460L;
	private Feed feed1;
	private FeedDoc feedDoc;

	private static Calendar theDate;
	static {
		theDate = Calendar.getInstance();
		theDate.clear();
		theDate.set(2008, 0, 1);
	}

	private String mega = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\" xmlns:local=\"http://purl.org/dc/elements/1.1/\">"
			+ "<id local:something=\"testVal\">http://colorfulsoftware.localhost/projects/atomsphere/atom.xml</id>"
			+ "<updated local:somethingElse=\"fakeValue\">2007-03-08T20:52:40.70-06:00</updated>"
			+ "<generator uri=\"http://www.colorfulsoftware.com/projects/atomsphere\" version=\"1.0.20\">Atomsphere</generator>"
			+ "<title type=\"xhtml\">Atomsphere a <b>great atom 1.0 parser </b></title>  <subtitle>a java atom feed library</subtitle>"
			+ "<author local:testAttr=\"testVal\"><test:test xmlns:test=\"http://www.w3.org/1999/test\" /><name>Bill Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></author>"
			+ "<contributor><name>Other Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></contributor>"
			+ "<contributor local:testAttr=\"testVal\"><test:test xmlns:test=\"http://www.w3.org/1999/test\" /><name>Bill Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></contributor>"
			+ "<category term=\"math\" scheme=\"http://www.colorfulsoftware.com/projects/atomsphere/\" label=\"math\" />"
			+ "<category term=\"science\" scheme=\"http://www.colorfulsoftware.com/projects/atomsphere/\" label=\"science\"/>"
			+ "<category term=\"thing\" />"
			+ "<link href=\"http://www.colorfulsoftware.com/projects/atomsphere/atom.xml\" rel=\"self\" type=\"application/atom+xml\" hreflang=\"UTF-8\" title=\"cool site\" />"
			+ "<icon local:testAttr=\"testVal\">http://www.minoritydirectory.net/images/logo.gif</icon>"
			+ "<logo local:testAttr=\"testVal\">http://www.minoritydirectory.net/images/logo.gif</logo>"
			+ "<rights xmlns=\"http://www.w3.org/2005/Atom\">Copyright 2007</rights>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\"><id>http://colorfulsoftware.localhost/colorfulsoftware/projects/atomsphere/atom.xml#About</id>"
			+ "<updated>2007-03-02T13:00:00.699-06:00</updated><title>About</title><published xmlns=\"http://www.w3.org/2005/Atom\">2007-02-26T12:34:01.330-06:00</published>"
			+ "<summary>About the project</summary>"
			+ "<author local:testAttr=\"testVal\"><test:test xmlns:test=\"http://www.w3.org/1999/test\" /><name>Bill Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></author>"
			+ "<contributor><name>Other Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></contributor>"
			+ "<contributor local:testAttr=\"testVal\"><test:test xmlns:test=\"http://www.w3.org/1999/test\" /><name>Bill Brown</name><uri>http://www.colorfulsoftware.com</uri><email>info@colorfulsoftware.com</email></contributor>"
			+ "<category term=\"math\" scheme=\"http://www.colorfulsoftware.com/projects/atomsphere/\" label=\"math\" />"
			+ "<category term=\"science\" scheme=\"http://www.colorfulsoftware.com/projects/atomsphere/\" label=\"science\"/>"
			+ "<link href=\"http://www.colorfulsoftware.com/projects/atomsphere/atom.xml\" rel=\"self\" type=\"application/atom+xml\" hreflang=\"UTF-8\" title=\"cool site\" />"
			+ "<content type=\"html\">&lt;ul&gt; &lt;li&gt;&lt;span class=\"boldText\"&gt;Atomsphere&lt;/span&gt; isa java library that allows you to create and modify atom 1.0 feeds.&lt;/li&gt; &lt;li&gt;It is distributed under the GNU GPL license and can be used in any manner complient with the license.&lt;/li&gt; &lt;li&gt;It is also packaged as a servlet-lib for use in web applications.&lt;/li&gt; &lt;li&gt;It is also packaged as a customtag library to display feeds on a webapage.&lt;/li&gt; &lt;li&gt;It also comes with an example webapp which demonstrates some example uses of the library.&lt;/li&gt; &lt;li&gt;It is written to be tied as closely as possible to the current atom specification found &lt;a href=\"http://www.atomenabled.org/developers/syndication/atom-format-spec.php\"&gt;here&lt;/a&gt;.&lt;/li&gt; &lt;/ul&gt;</content>"
			+ "<local:element>someitng that is an extension</local:element>"
			+ "</entry>"
			+ "<entry><id>http://colorfulsoftware.localhost/colorfulsoftware/projects/atomsphere/atom.xml#Requirements</id>"
			+ "<updated>2007-03-02T12:59:54.274-06:00</updated><title>Requirements</title><published>2007-02-26T12:58:53.197-06:00</published>"
			+ "<summary>Requirements for using the libraries</summary>"
			+ "<content type=\"html\">&lt;br /&gt;the project is usable with jdk 1.4.2 and above&lt;br /&gt; &amp;nbsp;&lt;br /&gt; needed for using the library&lt;br /&gt; &lt;ul&gt; &lt;li&gt;&lt;a href=\"https://sjsxp.dev.java.net/\"&gt;jsr173&lt;/a&gt; (STAX api jar) - see the &lt;a href=\"http://jcp.org/aboutJava/communityprocess/final/jsr173/index.html\"&gt;API&lt;/a&gt;.&lt;/li&gt; &lt;li&gt;&lt;a href=\"https://sjsxp.dev.java.net/\"&gt;sjsxp&lt;/a&gt; (STAX implementation) - others implementations may work but have not been tested.&lt;/li&gt; &lt;li&gt;&lt;a href=\"https://stax-utils.dev.java.net/\"&gt;stax-utils&lt;/a&gt; (for pretty printing)&lt;/li&gt; &lt;/ul&gt; needed for using the atomsphere-taglib&lt;br /&gt; &lt;ul&gt; &lt;li&gt;the atomsphere library&lt;/li&gt; &lt;li&gt;Any J2EE Servlet Container&lt;/li&gt; &lt;/ul&gt; needed for using the atomsphere-weblib&lt;br /&gt; &lt;ul&gt; &lt;li&gt;the atomsphere library&lt;/li&gt; &lt;li&gt;Any J2EE Servlet Container&lt;/li&gt; &lt;/ul&gt; needed for using the example atomsphere-webapp&lt;br /&gt; &lt;ul&gt; &lt;li&gt;Any J2EE Servlet Container&lt;/li&gt;&lt;/ul&gt;</content>"
			+ "</entry>"
			+ "<entry xmlns:test=\"http://www.w3.org/1999/test\"><id>http://colorfulsoftware.localhost/colorfulsoftware/projects/atomsphere/atom.xml#Documentation</id>"
			+ "<updated>2007-03-02T12:59:45.475-06:00</updated><title>Documentation</title><published>2007-02-26T13:00:00.478-06:00</published>"
			+ "<summary>Starting Documentation</summary>"
			+ "<test:test xmlns:test=\"http://www.w3.org/1999/test\">this is an extension test <test:does> it work? </test:does> we'll see</test:test>"
			+ "<content type=\"html\">&lt;h4&gt;Installation (atomsphere library)&lt;/h4&gt; &lt;ul&gt; &lt;li&gt;Add the jsr173, sjsxp, stax-utils and atomsphere jars to the classpath (WEB-INF/lib for webapps).&lt;/li&gt; &lt;/ul&gt; &lt;h4&gt;Installation (atomsphere-taglib)&lt;/h4&gt; &lt;ul&gt; &lt;li&gt;Add the atomsphere jar to the classpath (WEB-INF/lib for webapps).&lt;/li&gt;&lt;li&gt;Add the atomsphereTags.tld tag descriptor to the top of the jsp page (See example below). &lt;/li&gt; &lt;li&gt;Add anyrequired attributes and optional attributes to the custom tag (See example below).&lt;/li&gt; &lt;li&gt;View the atomsphereTags.tld for a description of the attributes and what they do.&lt;/li&gt;&lt;/ul&gt; &lt;h4&gt;Installation (atomsphere-weblib)&lt;/h4&gt; &lt;ul&gt;&lt;li&gt;Add the atomsphere and atomsphere-weblib jars to the classpath (WEB-INF/lib for webapps).&lt;/li&gt;&lt;li&gt;Copy the web-fragrment.xml (embeded in the jar file) to your application's web.xml file.&lt;/li&gt;&lt;/ul&gt; &lt;h4&gt;Installation (atomsphere-webapp)&lt;/h4&gt; &lt;ul&gt; &lt;li&gt;Deploy the war file to any J2EE servlet container.&lt;/li&gt; &lt;/ul&gt;</content>"
			+ "</entry>" + "</feed>";

	private String expectedEntry1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>" + "</entry>";

	private String badEntry1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\" id=\"notCool\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>" + "</entry>";

	private String expectedFeed1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String expectedFeed2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>"
			+ " <local:test xmlns=\"http://purl.org/dc/elements/1.1/\">things</local:test>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String badFeed1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>" + " <local:></local:>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private String badFeed2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/\">"
			+ " <title>Example Feed</title>"
			+ " <subtitle>A subtitle.</subtitle>"
			+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
			+ " <link href=\"http://example.org/\"/>" + " <></>"
			+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
			+ " <author>" + "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
			+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
			+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
			+ "   <updated>2003-12-13T18:30:02Z</updated>"
			+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";
	
	private String badFeed3 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
		+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/\">"
		+ "<generator uri=\"http://www.colorfulsoftware.com/projects/atomsphere\" version=\"1.0.20\" nono=\"nono\">Atomsphere</generator>"
		+ " <title>Example Feed</title>"
		+ " <subtitle>A subtitle.</subtitle>"
		+ " <link href=\"http://example.org/feed/\" rel=\"self\"/>"
		+ " <link href=\"http://example.org/\"/>"
		+ " <local:test xmlns=\"http://purl.org/dc/elements/1.1/\">things</local:test>"
		+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
		+ " <author>" + "   <name>John Doe</name>"
		+ "   <email>johndoe@example.com</email>" + " </author>"
		+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
		+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
		+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
		+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
		+ "   <updated>2003-12-13T18:30:02Z</updated>"
		+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";
	
	private String badFeed4 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
		+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/\">"
		+ " <title>Example Feed</title>"
		+ " <subtitle>A subtitle.</subtitle>"
		+ " <link rel=\"self\"/>"
		+ " <link href=\"http://example.org/\"/>"
		+ " <local:test xmlns=\"http://purl.org/dc/elements/1.1/\">things</local:test>"
		+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
		+ " <author>" + "   <name>John Doe</name>"
		+ "   <email>johndoe@example.com</email>" + " </author>"
		+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
		+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
		+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
		+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
		+ "   <updated>2003-12-13T18:30:02Z</updated>"
		+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";
	
	private String badFeed5 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
		+ "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:local=\"http://purl.org/dc/elements/1.1/\">"
		+ " <title>Example Feed</title>"
		+ " <subtitle>A subtitle.</subtitle>"
		+ " <link />"
		+ " <link href=\"http://example.org/\"/>"
		+ " <local:test xmlns=\"http://purl.org/dc/elements/1.1/\">things</local:test>"
		+ " <updated xml:lang=\"en-US\">2003-12-13T18:30:02Z</updated>"
		+ " <author>" + "   <name>John Doe</name>"
		+ "   <email>johndoe@example.com</email>" + " </author>"
		+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
		+ " <entry>" + "   <title>Atom-Powered Robots Run Amok</title>"
		+ "   <link href=\"http://example.org/2003/12/13/atom03\"/>"
		+ "   <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
		+ "   <updated>2003-12-13T18:30:02Z</updated>"
		+ "   <summary>Some text.</summary>" + " </entry>" + "</feed>";

	private Entry entry1, entry2, entry3;

	// found these examples here:
	// http://www.xml.com/pub/a/2005/12/07/handling-atom-text-and-content-constructs.html
	// that should apply to text constructs:
	// title, subtitle, summary, rights.
	private String title1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title>One bold foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String title2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title type=\"text\">One bold foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	// bad example here. the code actually unencodes the text.
	private String title3 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title>One &lt;strong&gt;bold&lt;/strong&gt; foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String title4 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title type=\"html\">One &lt;strong&gt;bold&lt;/strong&gt; foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String badCat1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title>One bold foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "<category baseball=\"no\">so what</category>" + "</feed>";

	private String badCat2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title>One bold foot forward</title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "<category>so what</category>" + "</feed>";
	/*
	 * currently of the 3 implementations tested: sjsxp stax woodstox None of
	 * them are able to detect CDATA sections. so the markup display ends up
	 * being escaped in the output without the cdata section.
	 */
	private String title5 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title type=\"html\"><![CDATA[One <strong>bold</strong> foot forward]]></title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String title6 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\">"
			+ "<title type=\"xhtml\"><div xmlns=\"http://www.w3.org/1999/xhtml\">One <strong>bold</strong> foot forward<title>can you see me</title></div></title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String title7 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\"  xmlns:xh=\"http://www.w3.org/1999/xhtml\">"
			+ "<title type=\"xhtml\"><xh:div>One <xh:strong>bold</xh:strong> foot forward </xh:div></title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";

	private String brokeTitle1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<feed xmlns=\"http://www.w3.org/2005/Atom\"  xmlns:xh=\"http://www.w3.org/1999/xhtml\">"
			+ "<title type=\"xhtml\" fakeAttr=\"bunk\"><xh:div>One <xh:strong>bold</xh:strong> foot forward </xh:div></title>"
			+ " <updated>2003-12-13T18:30:02Z</updated>" + " <author>"
			+ "   <name>John Doe</name>"
			+ "   <email>johndoe@example.com</email>" + " </author>"
			+ " <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>"
			+ "</feed>";
	// /

	// mising id
	private String brokenEntry1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			// + "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>" + "</entry>";

	// missing title
	private String brokenEntry2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated></entry>";
	// + "<title>test entry 1</title>" + "</entry>";

	// missing updated
	private String brokenEntry3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			// + "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>" + "</entry>";

	// missing summary attribute for empty content element
	private String brokenEntry4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>"
			+ "<content src=\"missingSummaryAttr\" />" + "</entry>";

	// bad content content type
	private String brokenEntry5 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>"
			+ "<content type=\"pdf\">this is no good</content>" + "</entry>";

	// unsupported attribute
	private String brokenEntry6 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<entry xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en-US\" fakeAttribute=\"noGood\">"
			+ "<id>http://www.colorfulsoftware.com/projects/atomsphere/</id>"
			+ "<updated>2008-01-01T00:00:00.00-06:00</updated>"
			+ "<title>test entry 1</title>" + "</entry>";

	/**
	 * @throws Exception
	 *             if there is an error creating the test data.
	 */
	@Before
	public void setUp() throws Exception {
		try {
			feedDoc = new FeedDoc();
			Id id = feedDoc.buildId(null,
					"http://www.colorfulsoftware.com/atom.xml");

			Updated updated = feedDoc.buildUpdated(Calendar.getInstance()
					.getTime(), null);

			Title title = feedDoc.buildTitle("test feed", null);

			Generator generator = feedDoc.getAtomsphereVersion();

			List<Author> authors = new LinkedList<Author>();
			authors.add(feedDoc.buildAuthor(feedDoc.buildName("Bill Brown"),
					null, null, null, null));

			try {
				feedDoc.buildAuthor(null, null, null, null, null);
				fail("should not get here;");
			} catch (Exception e) {
				assertTrue(e instanceof AtomSpecException);
				assertEquals(e.getMessage(),
						"Person constructs MUST contain exactly one \"atom:name\" element.");
			}

			try {
				List<Attribute> attrs = new LinkedList<Attribute>();
				attrs.add(feedDoc.buildAttribute("goofy", "attrValue"));
				feedDoc.buildAuthor(new Name("You"), null, null, attrs, null);
				fail("should not get here;");
			} catch (Exception e) {
				assertTrue(e instanceof AtomSpecException);
				assertEquals(e.getMessage(),
						"Unsupported attribute goofy for this Atom Person Construct.");
			}

			feed1 = feedDoc.buildFeed(id, title, updated, null, authors, null,
					null, null, null, null, generator, null, null, null, null);

			entry1 = feedDoc.buildEntry(feedDoc.buildId(null,
					"http://www.colorfulsoftware.com/projects/atomsphere/"),
					feedDoc.buildTitle("test entry 1", null), feedDoc
							.buildUpdated(theDate.getTime(), null), null, null,
					null, null, null, null, null, null, null, null, null);

			entry2 = feedDoc.buildEntry(feedDoc.buildId(null,
					"http://www.colorfulsoftware.com/projects/atomsphere/"),
					feedDoc.buildTitle("test entry 2", null), feedDoc
							.buildUpdated(theDate.getTime(), null), null, null,
					null, null, null, null, null, null, null, null, null);

			entry3 = feedDoc.buildEntry(feedDoc.buildId(null,
					"http://www.colorfulsoftware.com/projects/atomsphere/"),
					feedDoc.buildTitle("test entry 3", null), feedDoc
							.buildUpdated(theDate.getTime(), null), null, null,
					null, null, null, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws Exception
	 *             if there is an error cleaning up the test data.
	 */
	@After
	public void tearDown() throws Exception {
		new File("target/out.xml").deleteOnExit();
		new File("target/out2.xml").deleteOnExit();
	}

	/**
	 * test the output stream functionality.
	 */
	@Test
	public void testWriteFeedDocOutputStreamFeedStringString() {
		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://www.rand.org/news/press/index.xml"));
			feedDoc.writeFeedDoc(new FileOutputStream(
					"src/test/resources/out1.xml"), feed1, feedDoc
					.getEncoding(), feedDoc.getXmlVersion());
			Feed feed2 = feedDoc.readFeedToBean(new File(
					"src/test/resources/out1.xml"));
			assertNotNull(feed2);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
		}

		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://www.rand.org/news/press/index.xml"));
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream("target/out2.xml"), "UTF-8");
			feedDoc.writeFeedDoc(writer, feed1, "UTF-8", feedDoc
					.getXmlVersion());
			Feed feed2 = feedDoc.readFeedToBean(new File("target/out2.xml"));
			assertNotNull(feed2);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
		}

		/*
		 * uncomment stax-utils dependency in the root pom.xml to see example in
		 * action try { feed1 = feedDoc.readFeedToBean(new java.net.URL(
		 * "http://www.rand.org/news/press/index.xml"));
		 * feedDoc.writeFeedDoc(new IndentingXMLStreamWriter(XMLOutputFactory
		 * .newInstance().createXMLStreamWriter( new
		 * FileOutputStream("target/out3.xml"), "UTF-8")), feed1,
		 * feedDoc.encoding, feedDoc.xml_version); Feed feed2 =
		 * feedDoc.readFeedToBean(new File("target/out3.xml"));
		 * assertNotNull(feed2); } catch (Exception e) { e.printStackTrace();
		 * assertTrue(e instanceof XMLStreamException); }
		 */
	}

	/**
	 * test the output stream functionality.
	 */
	@Test
	public void testWriteEntryDocOutputStreamEntryStringString() {
		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://www.rand.org/news/press/index.xml"));
			feedDoc.writeEntryDoc(new FileOutputStream(
					"src/test/resources/out1.xml"), feed1.getEntries().get(
					feed1.getEntries().firstKey()), feedDoc.getEncoding(),
					feedDoc.getXmlVersion());
			Entry entry1 = feedDoc.readEntryToBean(new File(
					"src/test/resources/out1.xml"));
			assertNotNull(entry1);
		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://www.rand.org/news/press/index.xml"));
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream("target/out2.xml"), "UTF-8");
			feedDoc.writeEntryDoc(writer, feed1.getEntries().get(
					feed1.getEntries().firstKey()), "UTF-8", feedDoc
					.getXmlVersion());
			Entry entry1 = feedDoc.readEntryToBean(new File("target/out2.xml"));
			assertNotNull(entry1);
		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here.");
		}
	}

	/**
	 * test the stream writer functionality.
	 */
	@Test
	public void testWriteFeedDocXMLStreamWriterFeedStringString() {
		try {
			// pretty print version.
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://www.rand.org/news/press/index.xml"));
			feedDoc.writeFeedDoc(XMLOutputFactory.newInstance()
					.createXMLStreamWriter(
							new FileOutputStream("target/out2.xml"),
							feedDoc.getEncoding()), feed1, feedDoc
					.getEncoding(), feedDoc.getXmlVersion());
			Feed feed2 = feedDoc.readFeedToBean(new File("target/out2.xml"));
			assertNotNull(feed2);
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
		}
	}

	/**
	 * test the string reading functionality.
	 */
	@Test
	public void testReadFeedToStringFeedString() {
		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://www.rand.org/news/press/index.xml"));
			String feedStr = feedDoc.readFeedToString(feed1,
					"com.sun.xml.txw2.output.IndentingXMLStreamWriter");
			assertNotNull(feedStr);
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
		}

		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://www.rand.org/news/press/index.xml"));
			String feedStr = feedDoc.readFeedToString(feed1,
					"javanet.staxutils.IndentingXMLStreamWriter");
			assertNotNull(feedStr);
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
		}

		try {
			feed1 = feedDoc.readFeedToBean(new java.net.URL(
					"http://www.rand.org/news/press/index.xml"));
			String feedStr = feedDoc.readFeedToString(feed1, "bunk");
			assertNotNull(feedStr);
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
		}
	}

	/**
	 * test the string reading functionality.
	 */
	@Test
	public void testReadFeedToStringFeed() {
		try {
			feed1 = feedDoc.readFeedToBean(expectedFeed1);
			String feed1Str = feedDoc.readFeedToString(feed1);
			assertNotNull(feed1Str);
			feed1 = feedDoc.readFeedToBean(feed1Str);
			assertNotNull(feed1);
			assertNotNull(feed1.getId());
			assertNotNull(feed1.getTitle());
			Updated updated = feed1.getUpdated();
			assertNotNull(updated);
			assertNotNull(updated.getAttribute("xml:lang"));
			assertNull(updated.getAttribute("bunky"));

		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
		}

		try {
			feed1 = feedDoc.readFeedToBean(expectedFeed2);
			assertNotNull(feed1.getExtension("local:test")
					.getAttribute("xmlns"));
			assertNull(feed1.getExtension("local:test").getAttribute("bunky"));

		} catch (Exception e) {
			fail("should not get here.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(badFeed1);
			fail("should not get here.");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Extension element names SHOULD NOT be null and SHOULD NOT be blank.");
		}

		try {
			feed1 = feedDoc.readFeedToBean(badFeed2);
			fail("should not get here.");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof javax.xml.stream.XMLStreamException);
		}
		
		try {//bad generator
			feed1 = feedDoc.readFeedToBean(badFeed3);
			fail("should not get here.");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),"Unsupported attribute nono in the atom:generator element.");
		}
		
		try {//bad link no href
			feed1 = feedDoc.readFeedToBean(badFeed4);
			fail("should not get here.");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),"atom:link elements MUST have an href attribute, whose value MUST be a IRI reference.");
		}
		
		try {//bad link empty
			feed1 = feedDoc.readFeedToBean(badFeed5);
			fail("should not get here.");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),"atom:link elements MUST have an href attribute, whose value MUST be a IRI reference.");
		}

		// test the seven title variants.
		try {

			feed1 = feedDoc.readFeedToBean(title1);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(), "One bold foot forward");
			assertNotNull(feedDoc.readFeedToString(feed1));

			feed1 = feedDoc.readFeedToBean(title2);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(), "One bold foot forward");
			assertNotNull(feedDoc.readFeedToString(feed1));

			feed1 = feedDoc.readFeedToBean(title3);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(),
					"One <strong>bold</strong> foot forward");
			assertNotNull(feedDoc.readFeedToString(feed1));

			feed1 = feedDoc.readFeedToBean(title4);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(),
					"One &lt;strong&gt;bold&lt;/strong&gt; foot forward");
			assertNotNull(feedDoc.readFeedToString(feed1));

			/*
			 * currently of the 3 implementations tested: sjsxp stax woodstox
			 * None of them are able to detect CDATA sections.
			 */
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = inputFactory
					.createXMLStreamReader(new java.io.StringReader(title5));
			FeedReader.checkForCDATA(reader);

			feed1 = feedDoc.readFeedToBean(title5);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(),
					"One &lt;strong&gt;bold&lt;/strong&gt; foot forward");
			assertNotNull(feedDoc.readFeedToString(feed1));

			feed1 = feedDoc.readFeedToBean(title6);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(),
					"One <strong>bold</strong> foot forward<title>can you see me</title>");
			assertNotNull(feedDoc.readFeedToString(feed1));

			feed1 = feedDoc.readFeedToBean(title7);
			assertNotNull(feed1.getTitle());
			assertEquals(feed1.getTitle().getText(),
					"One <xh:strong>bold</xh:strong> foot forward ");
			assertNotNull(feed1.getTitle().getAttribute("type"));
			assertNull(feed1.getTitle().getAttribute("bunk"));
			assertNotNull(feedDoc.readFeedToString(feed1));

			try {
				feed1 = feedDoc.readFeedToBean(brokeTitle1);
				fail("should not get here.");
			} catch (Exception e) {
				assertTrue(e instanceof AtomSpecException);
				assertEquals(e.getMessage(),
						"Unsupported attribute fakeAttr for this Atom Text Construct.");
			}

			try {
				feed1 = feedDoc.readFeedToBean(badCat1);
				fail("should not get here.");
			} catch (Exception e) {
				assertTrue(e instanceof AtomSpecException);
				assertEquals(e.getMessage(),
						"Unsupported attribute baseball in the atom:category element.");
			}

			try {
				feed1 = feedDoc.readFeedToBean(badCat2);
				fail("should not get here.");
			} catch (Exception e) {
				assertTrue(e instanceof AtomSpecException);
				assertEquals(e.getMessage(),
						"Category elements MUST have a \"term\" attribute.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e instanceof AtomSpecException);
		}
	}

	/**
	 * test the string reading functionality.
	 */
	@Test
	public void testReadEntryToString() {
		try {
			String entryStr = feedDoc.readEntryToString(feedDoc
					.readEntryToBean(expectedEntry1));
			assertTrue(entryStr != null);
			assertEquals(entryStr, expectedEntry1);
		} catch (Exception e) {
			e.printStackTrace();
			fail("this should not happen.");
		}

		try {
			String entryStr = feedDoc.readEntryToString(entry1,
					"javanet.staxutils.IndentingXMLStreamWriter");
			assertTrue(entryStr != null);
		} catch (Exception e) {
			fail("this should not happen.");
		}

		// test a bad writer.
		try {
			String entryStr = feedDoc.readEntryToString(entry1,
					"com.fake.BunkWriter");
			assertTrue(entryStr != null);
		} catch (Exception e) {
			fail("this should not happen.");
		}
	}

	/**
	 * test the string reading functionality.
	 */
	@Test
	public void testReadEntryToBeanString() {
		Entry entry;
		try {
			entry = feedDoc.readEntryToBean(brokenEntry1);
			assertTrue(entry == null);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:entry elements MUST contain exactly one atom:id element.");
		}

		try {
			entry = feedDoc.readEntryToBean(brokenEntry2);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:entry elements MUST contain exactly one atom:title element.");
		}

		try {
			entry = feedDoc.readEntryToBean(brokenEntry3);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:entry elements MUST contain exactly one atom:updated element.");
		}

		try {
			entry = feedDoc.readEntryToBean(brokenEntry4);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"atom:entry elements MUST contain an atom:summary element if the atom:entry contains an atom:content that has a \"src\" attribute (and is thus empty).");
		}

		try {
			entry = feedDoc.readEntryToBean(brokenEntry5);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Unsupported attribute type for this Atom Text Construct.");
		}

		try {
			entry = feedDoc.readEntryToBean(brokenEntry6);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Unsupported attribute fakeAttribute for this element.");
		}

		try {
			entry = feedDoc.readEntryToBean(expectedEntry1);
			assertNotNull(entry);
		} catch (Exception e) {
			fail("should not get here;");
		}

		try {
			entry = feedDoc.readEntryToBean(badEntry1);
			fail("should not get here;");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Unsupported attribute id for this element.");
		}

		try {
			entry = feedDoc.readEntryToBean(new File(
					"src/test/resources/expectedEntry1.xml"));
			assertNotNull(entry);
		} catch (Exception e) {
			fail("should not get here;");
		}

		try {
			entry = feedDoc.readEntryToBean(new URL(
					"http://www.earthbeats.net/drops.xml"));
			assertNotNull(entry);
		} catch (Exception e) {
			fail("should not get here;");
		}
	}

	/**
	 * test the feed building dates.
	 */
	@Test
	public void testBuildDate() {
		try {
			feedDoc.buildPublished(null, null);
			fail("should not get here.");
		} catch (AtomSpecException e) {
			assertEquals(e.getMessage(),
					"AtomDateConstruct Dates SHOULD NOT be null.");
		}

		try {
			Attribute err = feedDoc.buildAttribute("href", "bunk");
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(err);
			feedDoc.buildPublished(null, attrs);
			fail("should not get here.");
		} catch (AtomSpecException e) {
			assertEquals(e.getMessage(),
					"Unsupported attribute href for this Atom Date Construct.");
		}

		try {
			// roll the date back to a different timezone
			TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-10"));
			Published pub = feedDoc.buildPublished(Calendar.getInstance()
					.getTime(), null);
			assertTrue(pub.getText() != null);
			TimeZone.setDefault(null);
		} catch (AtomSpecException e) {
			fail("should not get here.");
		}
	}

	/**
	 * test the feed building functionality.
	 */
	@Test
	public void testBuildFeed() {
		try {
			Generator generator = feedDoc.getAtomsphereVersion();

			Id id = feedDoc.buildId(null,
					"http://www.colorfulsoftware.com/atom.xml");

			Updated updated = feedDoc.buildUpdated(Calendar.getInstance()
					.getTime(), null);

			Title title = feedDoc.buildTitle("test feed", null);

			List<Contributor> contributors = new LinkedList<Contributor>();
			Contributor contributor = feedDoc.buildContributor(new Name(
					"Mad Dog"), null, feedDoc.buildEmail("info@maddog.net"),
					null, null);
			contributors.add(contributor);

			Rights rights = feedDoc.buildRights("GPL 1.0", null);

			Icon icon = feedDoc.buildIcon(null, "http://host/images/icon.png");

			Logo logo = feedDoc.buildLogo(null, "http://host/images/logo.png");

			List<Attribute> catAttrs = new LinkedList<Attribute>();
			catAttrs.add(feedDoc.buildAttribute("term", "music"));
			catAttrs.add(feedDoc.buildAttribute("scheme",
					"http://mtv.com/genere"));
			catAttrs.add(feedDoc.buildAttribute("label", "music"));
			List<Category> categories = new LinkedList<Category>();
			Category category = feedDoc.buildCategory(catAttrs, null);
			categories.add(category);

			List<Attribute> linkAttrs = new LinkedList<Attribute>();
			linkAttrs.add(feedDoc
					.buildAttribute("href", "http://www.yahoo.com"));
			linkAttrs.add(feedDoc.buildAttribute("rel", "self"));
			linkAttrs.add(feedDoc.buildAttribute("hreflang", "en-US"));
			List<Link> links = new LinkedList<Link>();
			Link link = feedDoc.buildLink(linkAttrs, null);
			links.add(link);

			Attribute extAttr = feedDoc.buildAttribute("xmlns:xhtml",
					"http://www.w3.org/1999/xhtml");
			List<Attribute> extAttrs = new LinkedList<Attribute>();
			extAttrs.add(extAttr);

			// the base feed attributes.
			List<Attribute> feedAttrs = new LinkedList<Attribute>();
			feedAttrs.add(feedDoc.getAtomBase());
			feedAttrs.add(feedDoc.getLangEn());
			feedAttrs.addAll(extAttrs);

			List<Extension> extensions = new LinkedList<Extension>();
			Extension extension = feedDoc.buildExtension("xhtml:div", null,
					"<span style='color:red;'>hello there</span>");
			extensions.add(extension);

			List<Author> authors = new LinkedList<Author>();
			authors.add(feedDoc.buildAuthor(feedDoc.buildName("Bill Brown"),
					null, null, null, null));
			Entry entry = feedDoc
					.buildEntry(
							feedDoc
									.buildId(null,
											"http://www.colorfulsoftware.com/atom.xml#entry1"),
							feedDoc.buildTitle("an example atom entry", null),
							feedDoc.buildUpdated(Calendar.getInstance()
									.getTime(), null),
							null,
							feedDoc
									.buildContent(
											"Hello World.  Welcome to the atomsphere feed builder for atom 1.0 builds.  I hope it is useful for you.",
											null), authors, null, null, null,
							null, null, null, null, null);

			SortedMap<String, Entry> entries = new TreeMap<String, Entry>();
			entries.put(entry.getUpdated().getText(), entry);

			Feed feed = feedDoc.buildFeed(id, title, updated, rights, authors,
					categories, contributors, links, feedAttrs, extensions,
					generator, null, icon, logo, entries);

			assertNotNull(feed);
			assertNotNull(feed.getAttribute("xml:lang"));
			assertNull(feed.getAttribute("bunk"));

			// read and write a full feed.
			feed = feedDoc.readFeedToBean(mega);
			assertNotNull(feed.getId());
			assertNull(feed.getGenerator().getAttribute("notHere"));
			assertNotNull(feed.getAuthor("Bill Brown"));
			assertNotNull(feed.getContributor("Bill Brown"));
			assertNotNull(feed.getLink("http://www.colorfulsoftware.com/projects/atomsphere/atom.xml"));
			assertNotNull(feed.getId().getAttribute("local:something"));
			assertNull(feed.getId().getAttribute("bunk"));
			assertNull(feed.getCategory("math").getAttribute("anythingWrong"));
			assertNotNull(feed.getIcon().getAttribute("local:testAttr"));
			assertNotNull(feed.getLogo().getAttribute("local:testAttr"));
			assertNotNull(feed.getRights().getAttribute("xmlns"));
			assertNull(feed.getRights().getAttribute("sayWhat"));
			assertNotNull(feed.getUpdated().getDateTime());
			assertNull(feed.getSubtitle().getAttribute("sayWhat"));
			
			
			FeedWriter feedWriter = new FeedWriter();
			XMLStreamWriter writer = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(
							new FileOutputStream("target/dump1.xml"));
			feedWriter.writeFeed(writer, feed);
			writer.flush();
			writer.close();

			for (Entry ent : feed.getEntries().values()) {
				if (ent
						.getId()
						.getAtomUri()
						.equals(
								"http://colorfulsoftware.localhost/colorfulsoftware/projects/atomsphere/atom.xml#About")) {
					Author auth = ent.getAuthor("Bill Brown");
					assertNotNull(ent.getAttribute("xmlns"));
					assertNotNull(auth);
					assertNotNull(auth.getAttribute("local:testAttr"));
					Attribute attr1 = auth.getAttribute("local:testAttr");
					assertFalse(attr1.equals(auth.getAttribute("local:blank")));
					assertNull(auth.getAttribute("local:blank"));
					assertNotNull(auth.getExtension("test:test"));
					assertNull(auth.getExtension("local:bunky"));
					assertNull(ent.getAuthor("some other dude"));
					assertNotNull(ent.getContributor("Bill Brown"));
					assertNull(ent.getContributor("some other dude"));
					assertNotNull(ent.getCategory("science"));
					assertNull(ent.getCategory("nothing"));
					assertNotNull(ent
							.getLink("http://www.colorfulsoftware.com/projects/atomsphere/atom.xml"));
					assertNull(ent.getLink("http://www.fakeness.net"));
					assertNotNull(ent.getExtension("local:element"));
					assertNull(ent.getExtension("local:notthere"));
					assertNotNull(ent.getPublished().getAttribute("xmlns"));
					assertNull(ent.getSummary().getAttribute("sayWhat"));
					
					Contributor cont = ent.getContributor("Bill Brown");
					assertNotNull(cont);
					assertNotNull(cont.getExtension("test:test"));
					assertNotNull(cont.getAttribute("local:testAttr"));
					attr1 = cont.getAttribute("local:testAttr");
					assertFalse(attr1.equals(auth.getAttribute("local:blank")));
					assertNull(auth.getAttribute("local:blank"));
					assertNotNull(auth.getExtension("test:test"));
					assertNull(auth.getExtension("local:bunky"));
					assertNull(ent.getAuthor("some other dude"));
					assertNotNull(ent.getContributor("Bill Brown"));
					assertNull(ent.getContributor("some other dude"));
					assertNotNull(ent.getCategory("science"));
					assertNull(ent.getCategory("nothing"));
					assertNotNull(ent
							.getLink("http://www.colorfulsoftware.com/projects/atomsphere/atom.xml"));
					assertNull(ent.getLink("http://www.fakeness.net"));
					assertNotNull(ent.getExtension("local:element"));
					assertNull(ent.getExtension("local:notthere"));
				}
			}

			// re read the written feed and check the data.
			feed = feedDoc.readFeedToBean(new File("target/dump1.xml"));

			assertNotNull(feed);
			assertNotNull(feed.getCategories());
			List<Category> cats = feed.getCategories();
			for (Category cat : cats) {
				assertNotNull(cat.getTerm());
				if (!cat.getTerm().getValue().equals("thing")) {
					assertNotNull(cat.getLabel());
					assertNotNull(cat.getScheme());
				}
			}

			List<Link> lnks = feed.getLinks();
			for (Link lnk : lnks) {
				assertNotNull(lnk.getHref());
				assertNotNull(lnk.getHreflang());
				assertNotNull(lnk.getTitle());
				assertNotNull(lnk.getType());
				assertNotNull(lnk.getRel());
			}

			new File("target/dump1.xml").deleteOnExit();

		} catch (Exception e) {
			e.printStackTrace();
			fail("Issue building feed doc.");
		}

		// test a plain attribute
		try {
			feedDoc.buildAttribute(null, "null");
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"Attributes SHOULD NOT be null and SHOULD NOT be blank.");
		}

		Id id = null;
		Title title = null;
		Updated updated = null;
		SortedMap<String, Entry> entries = null;
		try {
			id = feedDoc.buildId(null, "http://www.test.com");
			title = feedDoc.buildTitle("title", null);
			updated = feedDoc.buildUpdated(Calendar.getInstance().getTime(),
					null);
			entries = new TreeMap<String, Entry>();
			entries.put(updated.getText(), feedDoc.buildEntry(id, title,
					updated, null, null, null, null, null, null, null, null,
					null, null, null));
		} catch (AtomSpecException e1) {
			e1.printStackTrace();
			fail("should not get here.");
		}

		try {// no id
			feedDoc.buildFeed(null, title, updated, null, null, null, null,
					null, null, null, null, null, null, null, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:feed elements MUST contain exactly one atom:id element.");
		}

		try {// no title
			feedDoc.buildFeed(id, null, updated, null, null, null, null, null,
					null, null, null, null, null, null, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:feed elements MUST contain exactly one atom:title element.");
		}

		try {// no updated
			feedDoc.buildFeed(id, title, null, null, null, null, null, null,
					null, null, null, null, null, null, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(e.getMessage(),
					"atom:feed elements MUST contain exactly one atom:updated element.");
		}

		try {// no authors
			feedDoc.buildFeed(id, title, updated, null, null, null, null, null,
					null, null, null, null, null, null, null);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"atom:feed elements MUST contain one or more atom:author elements, unless the atom:entry contains an atom:source element that contains an atom:author element or, in an Atom Feed Document, the atom:feed element contains an atom:author element itself.");
		}

		try {// no authors 2
			feedDoc.buildFeed(id, title, updated, null, null, null, null, null,
					null, null, null, null, null, null, entries);
			fail("should not get here.");
		} catch (Exception e) {
			assertTrue(e instanceof AtomSpecException);
			assertEquals(
					e.getMessage(),
					"atom:feed elements MUST contain one or more atom:author elements, unless all of the atom:feed element's child atom:entry elements contain at least one atom:author element.");
		}
	}

	/**
	 * test the getContentType functionality.
	 */
	@Test
	public void testGetContentType() {
		try {
			List<Attribute> attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("type", "image/gif"));

			Title title = feedDoc.buildTitle(null, attrs);
			assertEquals(title.getContentType(),
					AtomTextConstruct.ContentType.OTHER);

			attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("type", "text"));
			title = feedDoc.buildTitle(null, attrs);
			assertEquals(title.getContentType(),
					AtomTextConstruct.ContentType.TEXT);

			attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("type", "html"));
			title = feedDoc.buildTitle(null, attrs);
			assertEquals(title.getContentType(),
					AtomTextConstruct.ContentType.HTML);

			attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("type", "xhtml"));
			title = feedDoc.buildTitle(null, attrs);
			assertEquals(title.getContentType(),
					AtomTextConstruct.ContentType.XHTML);

			attrs = new LinkedList<Attribute>();
			attrs.add(feedDoc.buildAttribute("src",
					"http://www.colorfulsoftware.com/images/logo.gif"));
			Content content = feedDoc.buildContent(null, attrs);
			assertEquals(content.getContentType(),
					AtomTextConstruct.ContentType.EXTERNAL);

		} catch (AtomSpecException e) {
			e.printStackTrace();
			fail("this shouldn't happen");
		}
	}

	/**
	 * test sorting entries.
	 */
	@Test
	public void testSortEntries() {
		try {
			SortedMap<String, Entry> entries = new TreeMap<String, Entry>();
			String entryStr1 = entry1.getTitle().getText();
			String entryStr2 = entry2.getTitle().getText();
			String entryStr3 = entry3.getTitle().getText();
			entries.put(entryStr1, entry1);
			entries.put(entryStr2, entry2);
			entries.put(entryStr3, entry3);
			feed1 = feedDoc.buildFeed(feed1.getId(), feed1.getTitle(), feed1
					.getUpdated(), feed1.getRights(), feed1.getAuthors(), feed1
					.getCategories(), feed1.getContributors(),
					feed1.getLinks(), feed1.getAttributes(), feed1
							.getExtensions(), feed1.getGenerator(), feed1
							.getSubtitle(), feed1.getIcon(), feed1.getLogo(),
					entries);
			assertEquals(Title.class.getSimpleName(), "Title");
			feed1 = feedDoc.sortEntries(feed1, feedDoc.SORT_ASC, Title.class);
			for (Entry entry : feed1.getEntries().values()) {
				assertNotNull(entry);
			}
			SortedMap<String, Entry> entries2 = feed1.getEntries();
			assertEquals(entries2.firstKey(), entryStr1);
			entries2.remove(entries2.firstKey());
			assertEquals(entries2.firstKey(), entryStr2);
			entries2.remove(entries2.firstKey());
			assertEquals(entries2.firstKey(), entryStr3);

			feed1 = feedDoc.sortEntries(feed1, feedDoc.SORT_DESC, Title.class);
			for (Entry entry : feed1.getEntries().values()) {
				assertNotNull(entry);
			}
			entries2 = feed1.getEntries();
			assertEquals(entries2.firstKey(), entryStr3);
			entries2.remove(entries2.firstKey());
			assertEquals(entries2.firstKey(), entryStr2);
			entries2.remove(entries2.firstKey());
			assertEquals(entries2.firstKey(), entryStr1);

			// test the null entries case.
			feed1 = feedDoc.buildFeed(feed1.getId(), feed1.getTitle(), feed1
					.getUpdated(), feed1.getRights(), feed1.getAuthors(), feed1
					.getCategories(), feed1.getContributors(),
					feed1.getLinks(), feed1.getAttributes(), feed1
							.getExtensions(), feed1.getGenerator(), feed1
							.getSubtitle(), feed1.getIcon(), feed1.getLogo(),
					null);
			feed1 = feedDoc.sortEntries(feed1, feedDoc.SORT_DESC, Title.class);
			assertNull(feed1.getEntries());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
