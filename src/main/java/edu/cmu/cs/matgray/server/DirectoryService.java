/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.server;

import com.unboundid.ldap.sdk.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles access to the CMU LDAP directory
 */
public class DirectoryService {
    /**
     * Get the name and department associated with the AndrewID
     */
    public static String[] fields = {"cn", "cmuDepartment"};

    /**
     * Get attributes associated with the andrewID (Currently just the full
     * name and departement of the person
     *
     * @param andrewID the andrew id of the player
     * @return a list where the 0th element is the name of the person and the
     *         1st element is the department of the person
     * @throws LDAPException if there is a problem connecting to the CMU LDAP
     *                       server
     */
    public static List<String> getAttributes(String andrewID) throws
            LDAPException {

        List<String> attribs = new ArrayList<String>(2);

        /**
         * Create a new connection
         */
        LDAPConnection connection = new LDAPConnection("ldap.andrew.cmu.edu", 389);

        /**
         * For each field, query the server
         */
        for (String field : fields) {
            SearchResult searchResults = connection.search("dc=cmu,dc=edu",
                    SearchScope.SUB, "(cmuAndrewId=" + andrewID + ")", field);

            /**
             * In the case that there are multiple entries for a field,
             * just choose the first one
             */
            if (searchResults.getEntryCount() > 0) {
                SearchResultEntry entry = searchResults.getSearchEntries().get(0);
                attribs.add(entry.getAttributeValue(field));
            }
        }
        connection.close();
        return attribs;
    }
}