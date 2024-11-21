package com.example.views;

import com.webforj.annotation.InlineStyleSheet;
import com.webforj.component.Composite;
import com.webforj.component.Theme;
import com.webforj.component.button.Button;
import com.webforj.component.element.Element;
import com.webforj.component.html.elements.Div;
import com.webforj.component.html.elements.H1;
import com.webforj.component.html.elements.H3;
import com.webforj.component.html.elements.Img;
import com.webforj.component.html.elements.Paragraph;
import com.webforj.component.icons.Icon;
import com.webforj.component.icons.TablerIcon;
import com.webforj.component.layout.applayout.AppDrawerToggle;
import com.webforj.component.layout.applayout.AppLayout;
import com.webforj.component.tabbedpane.Tab;
import com.webforj.component.tabbedpane.TabbedPane;
import com.webforj.component.tabbedpane.TabbedPane.Placement;
import com.webforj.component.tabbedpane.event.TabSelectEvent;
import com.webforj.component.toast.Toast;
import com.webforj.router.annotation.FrameTitle;
import com.webforj.router.annotation.Route;

// @InlineStyleSheet("context://css/applayout/applayout.css")
@Route
@FrameTitle("AppLayout Multiple Headers")
public class AppLayoutView extends Composite<AppLayout> {

	AppLayout demo = getBoundComponent();
	Paragraph contentLabel = new Paragraph();

	Div header = new Div();
	Div drawer = new Div();
  Div dashboardContent = new Div();
  Div newEntryContent;
  Div editEntryContent = new Div();
  Div customersContent = new Div();

	public AppLayoutView() {

		demo.setDrawerHeaderVisible(true);
		demo.setDrawerFooterVisible(true);
    demo.setDrawerOpened(false);

		// Header
		header.addClassName("layout__header").add(
        new Img("ws://nexday icon.png"),
				new H3("MyTime - webforJ Edition"));
		demo.addToHeader(header);

		// Adding the additional toolbar with menu items
		Div secondToolbar = new Div();
		secondToolbar.addClassName("layout__second__header");
		demo.addToHeader(secondToolbar);
		TabbedPane secondMenu = new TabbedPane();
		secondToolbar.add(secondMenu);
		secondMenu.setBodyHidden(true);
		secondMenu.setBorderless(true);

		Icon dashboardIcon = TablerIcon.create("dashboard");
    Icon newEntryIcon = TablerIcon.create("files");
    Icon editEntryIcon = TablerIcon.create("file");
    Icon customersIcon = TablerIcon.create("users");

    secondMenu.addTab(new Tab("Dashboard", dashboardIcon));
    secondMenu.addTab(new Tab("New entry", newEntryIcon));
    secondMenu.addTab(new Tab("Edit entry", editEntryIcon));
		secondMenu.addTab(new Tab("Customers", customersIcon));

    secondMenu.onSelect(this::onTabChange);
    demo.addToContent(new H1(this.contentLabel));
    demo.addClassName("pageTitle");

    Button btn2 = new Button("Get age");
    demo.addToContent(dashboardContent);
    dashboardContent.add(btn2);

	}

	private void onTabChange(TabSelectEvent ev) {
		String value = ev.getTab().getText().replaceAll("<[^>]*>", "").trim();
		contentLabel.setText(value);
    if (value.equals("Dashboard")) {
      dashboardContent.setVisible(true);
      // newEntryContent.setVisible(false);
      editEntryContent.setVisible(false);
      customersContent.setVisible(false);
    }
    if (value.equals("New entry")) {
      dashboardContent.setVisible(false);
      editEntryContent.setVisible(false);
      customersContent.setVisible(false);
      if (newEntryContent == null) {
        Button btn3 = new Button("New entry");
        newEntryContent = new Div(btn3);
        demo.addToContent(newEntryContent);
        newEntryContent.setVisible(true);
      }
    }
    if (value.equals("Edit entry")) {
      dashboardContent.setVisible(false);
      newEntryContent.setVisible(false);
      editEntryContent.setVisible(true);
      customersContent.setVisible(false);
    }
    if (value.equals("Customers")) {
      dashboardContent.setVisible(false);
      newEntryContent.setVisible(false);
      editEntryContent.setVisible(false);
      customersContent.setVisible(true);
    }
	}
}
