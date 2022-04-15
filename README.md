# Birthday App

This app is a basic application, intended to help me familiarize myself with Android Development, as well as software engineering
as a whole. I aim to publish this application on the Google Play store eventually, to begin building a portfolio for myself. I will
update this file as I see fit, when the application has enough logic to become functional.

# Current problems

This app saves objects of type 'Birthday' in a Room database that is saved locally on the device. This means that the birthday entries are all inaccessable unless the user uses the device that stored the birthday entries. 

One solution to this problem is the use of Firebase. I will look further into this in the future, when my app is fully functional.

Another problem is that the current version of the app requires the user to store new birthdays. While this works fine, it is redundant, given the widespread use of reminder apps that are built into modern devices by default. An alternative would be a social-media type of application, where everyone is required to store only their own birthdays, and everyone could see everyone else's birthdays. I will call this birthday-v2.

# birthday-v2

This version of the app would ask the user for their own birthday, and store it in the local database. It would also allow them to update their own birthday. In the RecyclerView listing all the birthdays, it would display all the birthdays stored in the database, and it would allow users to highlight certain birthdays as their 'favorites'. The users could then navigate to another Fragment, where they could view a list of their own locally-stored favorite birthdays. 

This iteration of the app is reliant upon a cloud database, however, so I will complete my original idea first before tackling this one.
