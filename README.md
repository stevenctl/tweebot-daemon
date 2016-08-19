# tweebot-daemon

## about
This process schedules and executes periodic tasks for automatically liking, retweeting, and
following things on twitter. 

The accounts to do this on behalf on and what kind of content to like, etc. is 
stored in the tweebot-db. 

Users can manage their like/rt/follow policies using the tweebot-ui

The tweebot-api backs the UI for managing these policies.

## current features

* Auto-like tweets
* Auto-retweet tweets
* Auto-follow users
* Auto-mute the users that are auto-followed

## what I'd like to add

I'd like to add more functionality such as:

* activity log so you can see what tweebot has done for you
* setting up a tweet queue to post tweets at certain times
* setting a zip code and radius for your account to be effective in
* automatically sending thankyou DMs to those who follow you
* improvements to make sure we actually like as many tweets as possible
* better logging
* performance optimizations

* and more!
