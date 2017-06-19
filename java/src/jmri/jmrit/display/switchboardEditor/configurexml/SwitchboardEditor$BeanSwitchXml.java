package jmri.jmrit.display.switchboardEditor.configurexml;

import jmri.configurexml.AbstractXmlAdapter;
import jmri.jmrit.display.switchboardEditor.SwitchboardEditor;
import jmri.jmrit.display.switchboardEditor.SwitchboardEditor.BeanSwitch;
import jmri.jmrit.display.ToolTip;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle configuration for display.switchboard.BeanSwitch objects.
 *
 * @author Egbert Broerse Copyright: (c) 2017
 */
public class SwitchboardEditor$BeanSwitchXml extends AbstractXmlAdapter {

    public SwitchboardEditor$BeanSwitchXml() {
    }

    /**
     * Default implementation for storing the contents of a BeanSwitch.
     * Used the display Switchboard switches in JMRI web server.
     *
     * @param o Object to store, of type BeanSwitch
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {

        SwitchboardEditor.BeanSwitch bs = (SwitchboardEditor.BeanSwitch) o;

        Element element = new Element("beanswitch");

        // include attributes
        element.setAttribute("label", bs.getNameString());
        if (bs.getNamedBean() != null) {
            element.setAttribute("connected", "true");
        } else {
            element.setAttribute("connected", "false");
        }

        // get state textual info (only used for shape 'button')
        // includes beanswitch label to operate like SensorIcon
        // never null
        Element textElement = new Element("activeText");
        textElement.setAttribute("text", bs.getActiveText());
        element.addContent(textElement);

        textElement = new Element("inactiveText");
        textElement.setAttribute("text", bs.getInactiveText());
        element.addContent(textElement);

        textElement = new Element("unknownText");
        textElement.setAttribute("text", bs.getUnknownText());
        element.addContent(textElement);

        textElement = new Element("inconsistentText");
        textElement.setAttribute("text", bs.getInconsistentText());
        element.addContent(textElement);

        String txt = bs.getTooltip();
        if (txt != null) {
            Element elem = new Element("tooltip").addContent(txt);
            element.addContent(elem);
        }

        element.setAttribute("class", "jmri.jmrit.display.switchboardEditor.configurexml.SwitchboardEditor$BeanSwitchXml");

        return element;
    }

    /**
     * Load, starting with the BeanSwitch element, then all the value-icon
     * pairs.
     * Not currently used as BeanSwitches are autogenerated by settings
     *
     * @param element Top level Element to unpack.
     * @param o       an Editor as an Object
     */
    @Override
    public void load(Element element, Object o) {

        // create the objects
        //SwitchboardEditor.BeanSwitch bs = (SwitchboardEditor.BeanSwitch) o;

        log.debug("load xml called");
    }

    private final static Logger log = LoggerFactory.getLogger(SwitchboardEditor$BeanSwitchXml.class.getName());
}
