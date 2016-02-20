/** @file js_storage.js */

/**
 * Object for easy local storage usage.
 */
var SiteStorage = SiteStorage || {
	/**
	 * Adds data parameter to the local storage of the browser.
	 * @param tag variable name of data
	 * @param data data of the variable
	 */
	add: function(tag, data) {
		if(SiteStorage.isSupported() && typeof(data) !== "undefined")
			localStorage.setItem(tag, JSON.stringify(data));
	},
	
	/**
	 * Catches the variable from the local storage.
	 * @param tag variable name
	 * @returns undefined if the storage is not supported or variable is not in the storage
	 */
	get: function(tag) {
		if(SiteStorage.isSupported()) {
			var currentStorage = localStorage.getItem(tag);

			var ret = JSON.parse(currentStorage);
			
			return ret;
		}
		
		return undefined;
	},
	/**
	 * Checks if local storage is supported by user browser.
	 * @returns {Boolean}
	 */
	isSupported: function() { return typeof(Storage) !== "undefined"; }
}

// Unit tests
$(function(){
	SiteStorage.add("_testTag", ["rekt", "not_rekt"]);
	
	if(SiteStorage.get("_testTag")[0]!=="rekt")
		console.error("SiteStorage can't hold values. Perhaps browser does not support it?");
});
