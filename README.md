**** TESTING ****
Make sure to download and setup a WAMP server. You basically have to google WAMP server and the first or second link should have access to a page where you can download the installer.

Make sure to fill out localhost where it asks you to fill in something along with your email. If you are unsure, you can always google how to install wamp on google.

Once you have that running. you can go to localhost/phpmyadmin on a browser. There should be some sort of UI for you to be able to setup a database. Download the zip files for part 3.

Create a database by the name of "androidtest". The name is very important! Inside it you can go to the SQL tab and start creating tables from the zip file. You will have to take out the FOREIGN KEY portions of the SQL in order to be able to create the table at first. For FOREIGN KEYS there is something special you have to do.

1) Make the attribute an index. To do this, click on the Table from the left panel. There should be a Browse, Structure, SQL, etc. tabs. Click on Structure, and simly click the Index icon from the row attribute. MAKE SURE TO ONLY DO THIS ONCE!

2) You have to make sure that both keys are exactly the same, meaning that they are both ints and unsigned or of that similar fashion. (*****WARNING: when entering serial into the SQL portion, this is going to change the Type to bigint(20). Make sure they are all the same and along with UNSIGNED). 

3) After clicking the Structure tab, under the rows, there is a Relation View option. Click on the Relation View option. There should be a display of your PRIMARY KEYS and the Attributes you made into Indeces.

4) Click on the drop down to choose which TABLE its getting its attribute from. Then click on the key from the next column. If there was no ON DELETE or ON UPDATE specified in the file, change them to NO ACTION.

5) That should be everything for this portion. You're tables should be done and working. Remember they are all empty.



**** Getting the Files to your computer **** 

For the server files, all you need are the ones that are inside the server/android files. They are currently not complete, but if you want to see some samples. Download the files from samples. Move the PHP files into the www/ directory that WAMP created. You might need to download a git bash or cygwin to use a terminal. If I have a php file named "create_user.php", you can go to localhost/create_user.php as long as its in your www/ folder.

If it doesn't display anything, that means that there is something wrong with the connection to the database. Usually there can be connection errors, or an actual error will be displayed on the page.
