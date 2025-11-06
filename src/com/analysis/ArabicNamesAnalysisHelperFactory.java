package com.analysis;

/*
 * All source code and information in this file is made
 * available under the following licensing terms:
 *
 * Copyright (c) 2009, Palantir Technologies, Inc.
 * All rights reserved;
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     * Neither the name of Palantir Technologies, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.util.SwingWorker;

import com.palantir.api.dataevent.PalantirDataEventType;
import com.palantir.api.workspace.AbstractHelperFactory;
import com.palantir.api.workspace.ApplicationContext;
import com.palantir.api.workspace.ApplicationInterface;
import com.palantir.api.workspace.HelperFactory;
import com.palantir.api.workspace.HelperInterface;
import com.palantir.api.workspace.PalantirConnection;
import com.palantir.api.workspace.PalantirContext;
import com.palantir.api.workspace.PalantirFrame;
import com.palantir.api.workspace.applications.GraphApplicationInterface;
import com.palantir.client.search.SearchOperator;
import com.palantir.exception.PalantirSearchException;
import com.palantir.services.Locator;
import com.palantir.services.OperatorType;
import com.palantir.services.impl.search.ISearchQuery;
import com.palantir.services.loadlevel.LoadLevelFactory;
import com.palantir.services.ptobject.DataSourceRecord;
import com.palantir.services.ptobject.PTObjectContainer;
import com.palantir.services.ptobject.PTObjectContainerFactory;
import com.palantir.services.ptobject.PTObjectType;
import com.palantir.services.ptobject.Property;
import com.palantir.services.ptobject.PropertyType;
import com.palantir.services.ptobject.Role;
import com.palantir.services.ptobject.PTObject.SETTER_STYLE;
import com.palantir.services.search.SearchResultsPager;
import com.palantir.ui.TableLayouts;
import com.palantir.ui.component.ScaledImagePanel;
import com.palantir.util.BasicPTObjectContainerUtils;
import com.palantir.util.paging.ResultsPage;

/**
 * This ArabicNamesAnalysisHelperFactory is a factory which generates the ArabicNamesAnalysisHelper.
 *
 */
public class ArabicNamesAnalysisHelperFactory extends AbstractHelperFactory {

	public ArabicNamesAnalysisHelperFactory() {
		super("ArabicNamesAnalysisHelper Helper",
				new String[] { GraphApplicationInterface.APPLICATION_URI },
				new Integer [] { SwingConstants.VERTICAL },
				new Dimension(330,500),
				null,
		"com.arabic.ArabicNamesAnalysisHelperFactory");
	}

	/**
	 * <p>Creates an instance of the helper.</p>
	 *
	 * @param palantirContext the Palantir context for this helper
	 * @param application the application content
	 * @return a new helper
	 */
	public HelperInterface createHelper(PalantirContext palantirContext, ApplicationInterface application) {
		return new ArabicNamesAnalysisHelper(this, palantirContext, application);
	}

	protected static class ArabicNamesAnalysisHelper implements HelperInterface, ActionListener{

		private HelperFactory factory;
		private JTextField nameField;
		private JPanel panel;
		private PalantirContext palantirContext;
		private PalantirFrame palantirFrame;
		private JTextField oppositeName;
		private JTextField sexField;
		private JTextField derivationsField;
		private JTextField meaningField;

		@SuppressWarnings("serial")
		public ArabicNamesAnalysisHelper(HelperFactory factory, PalantirContext palantirContext, ApplicationInterface application) {
			this.factory = factory;
			this.palantirContext = palantirContext;


			panel = new JPanel(new BorderLayout());

			JPanel topPanel = new JPanel(TableLayouts.create("1,p,p","p,p,p,p", 8, 8));

			topPanel.setBackground(palantirContext.getColors().getSecondaryBackgroundColor());
			topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
			JLabel label = new JLabel("Request for Information");
			palantirContext.registerComponentForFontManagement(+4, label);
			topPanel.add(label, "1,0,2,0");

			JLabel l4 = new JLabel("Name:");
			palantirContext.registerComponentForFontManagement(0, l4);
			topPanel.add(l4, "1,1");

			nameField = new JTextField("Janet Jones");
			palantirContext.registerComponentForFontManagement(0, nameField);
			topPanel.add(nameField, "2,1");

			JLabel l2 = new JLabel("Forward Operating Base:");
			palantirContext.registerComponentForFontManagement(0, l2);
			topPanel.add(l2, "1,2");

			JComboBox box = new JComboBox(new Object[] {"Papa Tango", "Alpha Charlie Delta Mike", "Bravo", "Zulu"});
			palantirContext.registerComponentForFontManagement(0, box);
			topPanel.add(box, "2,2");

			JButton searchButton = new JButton("Search");
			searchButton.setActionCommand("SEARCH");
			searchButton.addActionListener(this);
			topPanel.add( searchButton, "1,3");

			JButton createButton = new JButton("Create");
			createButton.setActionCommand("CREATE");
			createButton.addActionListener(this);
			topPanel.add( createButton, "2,3");

			/** the analysing button  */
			JButton analyseButton = new JButton("Analysis");
			analyseButton.setActionCommand("ANALYSIS");
			analyseButton.addActionListener(this);
			topPanel.add( analyseButton, "1,4");

			/** add the name opposition label */
			JLabel oppositeLabel = new JLabel("Opposite Name:");
			palantirContext.registerComponentForFontManagement(0, oppositeLabel);
			topPanel.add(oppositeLabel, "1,5");

			/** add the name opposition field */
			oppositeName = new JTextField();
			palantirContext.registerComponentForFontManagement(0, oppositeName);
			oppositeName.setEditable(false);
			topPanel.add(oppositeName, "2,5");

			/** add the name sex type label */
			JLabel sexTypeLabel = new JLabel("Sex:");
			palantirContext.registerComponentForFontManagement(0, sexTypeLabel);
			topPanel.add(sexTypeLabel, "1,6");

			/** add the name sex type field */
			sexField = new JTextField();
			palantirContext.registerComponentForFontManagement(0, sexField);
			sexField.setEditable(false);
			topPanel.add(sexField, "2,6");

			/** add the name derivations label */
			JLabel derivationsLabel = new JLabel("Derivations:");
			palantirContext.registerComponentForFontManagement(0, derivationsLabel);
			topPanel.add(derivationsLabel, "1,7");

			/** add the name derivations field */
			derivationsField = new JTextField();
			palantirContext.registerComponentForFontManagement(0, derivationsField);
			derivationsField.setEditable(false);
			topPanel.add(derivationsField, "2,7");

			/** add the name meaning label */
			JLabel meaningLabel = new JLabel("Meaning:");
			palantirContext.registerComponentForFontManagement(0, meaningLabel);
			topPanel.add(meaningLabel, "1,8");

			/** add the name meaning field */
			meaningField = new JTextField();
			palantirContext.registerComponentForFontManagement(0, meaningField);
			meaningField.setEditable(false);
			topPanel.add(meaningField, "2,8");


			panel.setBackground(palantirContext.getColors().getPrimaryBackgroundColor());

			ScaledImagePanel image = new ScaledImagePanel(retrieveImage("/baghdad_tall.jpg")) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.setColor(new Color(255, 0, 0, 64));
					g.fillRect(30, 150, 260, 140);
					g.setColor(new Color(255, 0, 0, 255));
					g.drawRect(30, 150, 260, 140);
				}
			};
			image.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
			image.setLineBorderColor(Color.BLACK);
			panel.add(image, BorderLayout.CENTER);
			panel.add(topPanel, BorderLayout.NORTH);
		}

		public String getDefaultPosition() {
			return BorderLayout.EAST;
		}

		public JComponent getDisplayComponent() {
			return panel;
		}

		public Image getFrameIcon() {
			return null;
		}

		public void setOwners(PalantirFrame pFrame, ApplicationContext appContext) {
			this.palantirFrame = pFrame;
		}



		public Icon getIcon() {
			Icon myicon = new ImageIcon(retrieveImage("/small_world.gif"));
			return myicon;
		}


		public HelperFactory getFactory() {
			return factory;
		}

		public String getTitle() {
			return "ArabicNamesAnalysisHelper Helper";
		}

		public void initialize(ApplicationInterface app) {
			// do nothing
		}

		public void dispose(ApplicationInterface arg0) {
			// do nothing
		}


		public void setConstraint(String constraint) {
			// do nothing
		}


		private Image retrieveImage(String name) {
			Image theImage=null;
			try {
				theImage = new ImageIcon(ImageIO.read(ArabicNamesAnalysisHelperFactory.class.getResource(name))).getImage();
				return theImage;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return theImage;
		}

		public void actionPerformed(ActionEvent e) {

			if( e.getActionCommand().equals("SEARCH") ) {
				doSearch();
			}

			else if( e.getActionCommand().equals("CREATE")) {
				doCreate();
			}

			else if( e.getActionCommand().equals("ANALYSIS")) {
				doAnalyse();
			}

		}

		/**
		 * Search for a person with the name specified in the text field.  Add results
		 * to the Graph.
		 */
		private void doSearch() {

			final String name = this.nameField.getText();
			if( StringUtils.isEmpty(name)) {
				JOptionPane.showMessageDialog( palantirFrame.getFrame() , "No name specified");
				return;
			}

			final PalantirConnection conn = this.palantirContext.getPalantirConnection();

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {

					//new search
					ISearchQuery sq = palantirContext.getSearchFactory().getNewSearchQuery(OperatorType.INTERSECT );

					// by person
					sq.addObjectTypeTerm(PTObjectType.PERSON);

					PropertyType nameType = PropertyType.NAME;
					sq.addPropertyTerm(nameType, name, SearchOperator.EQUALS);

					// run the query (get pages of results)
					SearchResultsPager pager = conn.search(sq);

					ResultsPage<PTObjectContainer,PalantirSearchException> currentPage = pager;
					Collection<PTObjectContainer> ptocs = new ArrayList<PTObjectContainer>();
					while (currentPage.moreResultsAvailable()) {
						currentPage = currentPage.getNextPage();
					        ptocs.addAll(currentPage.getResults());
					}

					// Load the data
					Collection<Locator> locs = BasicPTObjectContainerUtils.toLocators(ptocs);
					ptocs = conn.objectLoad(locs, LoadLevelFactory.getFullyExceptDSRLoadedInstance());

					// if no results, notify user
					if( ptocs.isEmpty() ) {
						JOptionPane.showMessageDialog( palantirFrame.getFrame(), "No objects found with name: " + name );
						return null;
					}

					//add the results to the graph
					palantirContext.getGraph().addObjectsToGraph(ptocs);

					return null;
				}

			};

			// API safe way to do tasks that are investigation dependent
			palantirContext.getMonitoredExecutorService().execute(worker);

		}

		/**
		 * Create a person with the name specified in the text field.  Add result
		 * to the Graph.
		 */
		private void doCreate() {

			final String name = this.nameField.getText();
			if( StringUtils.isEmpty(name)) {
				JOptionPane.showMessageDialog( palantirFrame.getFrame() , "No name specified");
				return;
			}
			final PalantirConnection conn = this.palantirContext.getPalantirConnection();

			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {

					//create the data source records
					DataSourceRecord dsr = conn.getDsrFactory().createManuallyEnteredDsr();
					Collection<DataSourceRecord> dsrs = Collections.singleton(dsr);


					//create object
					PTObjectType ptocType = PTObjectType.PERSON;
					PTObjectContainer ptoc = PTObjectContainerFactory.createBlankObject(conn, ptocType );

					//set the title/label
					String label = name;
					ptoc.setTitle(label, conn, dsrs, SETTER_STYLE.KEEP_OTHERS, 1L);

					//set the name property
					PropertyType nameProp = PropertyType.NAME;
					Property p = Property.attemptToCreate(conn, nameProp, name, Role.NONE);
					p.addDataSourceRecord(conn.getDsrFactory().copyDsr(dsr));
					ptoc.addProperty(p);

					ArrayList<PTObjectContainer> ptocs = new ArrayList<PTObjectContainer>();
					ptocs.add( ptoc );

					// Store the object to the current investigative realm
					conn.objectStore(ptocs,PalantirDataEventType.DATA);

					// add to the graph
					palantirContext.getGraph().addObjectsToGraph(ptocs);

					return null;
				}

			};

			palantirContext.getMonitoredExecutorService().execute(worker);

		}

		/**
		 * - convert the name from it's language to the opposite language
		 * - determine if the name is belong to male or female
		 * - return the name derivations
		 * - return the meaning of the name
		 */
		private void doAnalyse() {

			final String name = this.nameField.getText();
			if( StringUtils.isEmpty(name)) {
				JOptionPane.showMessageDialog( palantirFrame.getFrame() , "No name specified");
				return;
			}

			String[] words = name.split(" ");
			String firstWord = words[0];

			AnalysisManager mgr = new AnalysisManager();
			try {
                String oppositedName = mgr.languageOpposition(firstWord);
                oppositeName.setText(oppositedName);

                boolean female = mgr.isFemale(oppositedName);
                sexField.setText(female ? "Female" : "Male");

                String derivations = mgr.processDerivations(oppositedName);
                derivationsField.setText(derivations);

                String meaning = mgr.processMeaning(oppositedName);
                meaningField.setText(meaning);
            } catch (AnalysisException e) {
                JOptionPane.showMessageDialog(palantirFrame.getFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

		}


	}

}
