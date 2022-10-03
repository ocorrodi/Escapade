# Escapade

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
1. [Schema](#Schema)

## Overview
### Description
Allows users to find and contact friends regarding vacation, gap year, and semester abroad ideas through friends sharing their past experiences.

### App Evaluation
- **Category:** Travel/Social Networking
- **Mobile:** This app would be primarily developed for mobile but would perhaps be just as viable on a computer. Functionality wouldn't be limited to mobile devices, however mobile version could potentially have more features.
- **Story:** Gives users an opportunity to browse friends' travel experiences and connect them for more information.
- **Market:** Any individual could choose to use this app, and to keep it a safe environment, people only see their Facebook friends' posts.
- **Habit:** This app could be used as often or unoften as the user wanted depending on their travel motivation/time, and what exactly they're looking for.
- **Scope:** First we would start with showing basic information from friends' experiences and then add a more complex suggestion algorithm and perhaps the ability to create/browse public posts.

## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can login and create an account
* User can view their basic profile information
* User can add a post with a title, location, images, and date
* User can view posts in a map view as well as in a list view
* Post details are shown when post is clicked

**Optional Nice-to-have Stories**

* Posts can be filtered and searched for
* Authentication with Facebook and connect with Facebook friends
* Posts can be "tagged"
* Like/favorite button for posts and place to view favorited posts
* User can view list of friends within the app
* User can view suggested experiences
* Clicking on a user shows their posts and ways to connect (FB messenger, email, etc.)

### 2. Screen Archetypes

* Login 
* Register - User signs up or logs into their account
   * Upon Download/Reopening of the application, the user is prompted to log in with the app and connect with Facebook to access Facebook friends
* Home Screen
   * Main screen with map view and list view of post results
   * Swipe to switch between map/list full screen view
   * Ability to search and filter posts
* Profile Screen 
   * Allows user view their profile information as well as their friends, posts, and suggested posts
* Add Post Screen
   * User can add a post with date, location, images, notes, and other information
   * Submitted posts will be shown in friends' home screen and suggestions
* Post Detail Screen
   * User can see more details about a post

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home
* Profile
* Add

Optional:
* Suggestions

**Flow Navigation** (Screen to Screen)
* Forced Log-in -> Account creation if no log in is available -> authentication with Facebooj
* Home Screen -> full screen map/list view -> post detail modal view or filter posts modal popup
* Profile -> display of friends, suggestions, or "my posts" in full screen

## Wireframes

![Wireframes 1](https://github.com/ocorrodi/FBU_App/blob/master/wireframes1.pdf)

![Wireframes 2](https://github.com/ocorrodi/FBU_App/blob/master/wireframes2.pdf)
