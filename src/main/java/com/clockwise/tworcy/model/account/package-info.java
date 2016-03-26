/**
 * {@link AccountDAOHibernate} uses {@link AccountData} to store data in database and converts it to {@link Account} before returning.
 * This method is used to avoid changing of {@link Account} object and make moving to different database types faster.
 * {@link AccountServiceHibernate} uses only {@link Account} which cannot be changed in any part of the application.
 * To change the {@link Account} object, one must first tell the service to update the data and get it back up.
 */
package com.clockwise.tworcy.model.account;