var version = "5.4.1";

var neo4jLeagueApp = angular.module('neo4jLeagueApp', [ 'ngResource', 'ngRoute' ]);

neo4jLeagueApp.config([ '$routeProvider', function($routeProvider, routeController) {
	$routeProvider.when('/:summonerId', {
		controller : 'routeController',
		templateUrl : 'templates/matches.html' // idk why, but this is needed
	});

	$routeProvider.when('/:summonerId/matches', {
		controller : 'lookupController',
		templateUrl : 'templates/matches.html'
	});

	$routeProvider.when('/:summonerId/ranked', {
		controller : 'lookupController',
		templateUrl : 'templates/ranked.html'
	});

	$routeProvider.when('/:summonerId/ranked-stats', {
		controller : 'lookupController',
		templateUrl : 'templates/ranked-stats.html'
	});
} ]);

neo4jLeagueApp.controller('routeController',
	function($rootScope, $routeParams, LeagueResource) {
		$rootScope.version = version;
		$rootScope.showTabs = false;

		LeagueResource.summonerFromId().get({
			id : $routeParams.summonerId
		}, function(data) {
			$rootScope.summoner = data;
			$rootScope.showTabs = true;
		}, function(error) {
			$rootScope.summoner = {};
		});

		$rootScope.showLookupResults = true;
		$rootScope.showTab = false;
	});

neo4jLeagueApp.controller('lookupController', function($scope, $rootScope, $routeParams,
	LeagueResource) {
	$rootScope.version = version;
	var lookupSummoner;

	if ($routeParams.summonerId) {
		if (!$rootScope.summoner) {
			LeagueResource.summonerFromId().get({
				id : $routeParams.summonerId
			}, function(data) {
				$rootScope.summoner = data;
				$rootScope.showTabs = true;
			}, function(error) {
				$rootScope.summoner = {};
			});
		}
		$rootScope.showLookupResults = true;
	}

	$rootScope.lookup = function() {
		$rootScope.showTabs = false;
		
		LeagueResource.lookupSummoner().get({
			name : $scope.summonerName
		}, function(data) {
			$rootScope.summoner = data;
			$rootScope.showTabs = true;
		}, function(error) {
			$rootScope.summoner = {};
		});
		$rootScope.showLookupResults = true;
		$rootScope.showTab = false;
	};

	$rootScope.lookupRanked = function(summonerId) {
		lookupSummoner = summonerId;
		clearData();

		LeagueResource.lookupRanked(summonerId).query({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner)
				setRankedMatches(summonerId, data);
		});
		$rootScope.showTab = true;
		$rootScope.showAll = false;
	};
	
	$rootScope.lookupAllRanked = function(summonerId) {
		lookupSummoner = summonerId;

		LeagueResource.allRanked(summonerId).query({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner)
				setRankedMatches(summonerId, data);
		});
		$rootScope.showTab = true;
		$rootScope.showAll = true;
	};
	
	var setRankedMatches = function(summonerId, data) {
		for(var i = 0; i < data.length; i++){
			var match = data[i];
			var players = match.players;
			for(var j = 0; j < players.length; j++)
				if(players[j].summoner.id == summonerId){
					match.lookupPlayer = j;
				}
		}
		
		$rootScope.newRanked = data;
	};

	$rootScope.lookupMatches = function(summonerId) {
		clearData();
		lookupSummoner = summonerId;

		LeagueResource.matchHistory(summonerId).query({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner) {
				for (var i = 0; i < data.length; i++) {
					if (data[i].teamId == 100)
						data[i].champion = data[i].blueTeam[0];	// Hella hacky... it works...
					else
						data[i].champion = data[i].redTeam[0];
				}
				$rootScope.newGames = data;
			}
		});
		$rootScope.showTab = true;
		$rootScope.showAll = false;
	};

	$rootScope.getRankedStats = function(summonerId) {
		clearData();
		lookupSummoner = summonerId;

		LeagueResource.rankedStats(summonerId).query({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner)
				$rootScope.rankedStats = data;
		});
		$rootScope.showTab = true;
	};

	$rootScope.lookupAllMatches = function(summonerId) {
		lookupSummoner = summonerId;

		LeagueResource.matchHistoryAll(summonerId).query({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner) {
				for (var i = 0; i < data.length; i++) {
					if (data[i].teamId == 100)
						data[i].champion = data[i].blueTeam[0];
					else
						data[i].champion = data[i].redTeam[0];
				}
				$rootScope.newGames = data;
			}
		});
		$rootScope.showTab = true;
		$rootScope.showAll = true;
	};

	// Used to check before pushing data if it's for the right match
	var expandedMatch = 0;

	$rootScope.expandMatch = function(match, summoner) {
		if (match.showExpand) {
			match.showExpand = false;
			return;
		}

		expandedMatch++;
		closeAllMatches($rootScope);
		match.showExpand = true;
	};

	var closeAllMatches = function($rootScope) {
		if ($rootScope.matchData != null)
			for (var i = 0; i < $rootScope.matchData.length; i++) {
				$rootScope.matchData[i].showExpand = false;
			}

		if ($rootScope.rankedData != null)
			for (var i = 0; i < $rootScope.rankedData.length; i++) {
				$rootScope.rankedData[i].showExpand = false;
			}
	};

	$rootScope.importAllRanked = function(summonerId) {
		$scope.working = ' (working)';
		$scope.disableImportButton = true;

		LeagueResource.allRanked().save({
			id : summonerId
		}, function(data) {
			$scope.working = ' (' + data.count + ' matches imported)';
			$rootScope.showAll = false;
		});
	};

	var clearData = function(){
		$rootScope.newGames = [];
		$rootScope.newRanked = [];
		$rootScope.rankedStats = [];
	};
	
	$scope.working = ' (slow)';
});

neo4jLeagueApp.service('LeagueResource', function($resource) {
	this.lookupSummoner = function() {
		return $resource('/azhu.lol/neo4j/rest/summoner/:name');
	};

	this.lookupSummoners = function() {
		return $resource('/azhu.lol/neo4j/rest/summoners/:ids');
	};

	this.summonerFromId = function() {
		return $resource('/azhu.lol/neo4j/rest/summoner/id/:id');
	};

	this.champFromId = function() {
		return $resource('/azhu.lol/rest/champion/:id');
	};

	this.summSpellFromId = function() {
		return $resource('/azhu.lol/rest/summoner-spell/:id');
	};

	this.allRanked = function() {
		return $resource('/azhu.lol/neo4j/rest/ranked-matches/:id/all', {
			id : '@id'
		});
	};

	this.lookupRanked = function() {
		return $resource('/azhu.lol/neo4j/rest/ranked-matches/:id', {
			id : '@id'
		});
	};

	this.matchHistory = function() {
		return $resource('/azhu.lol/neo4j/rest/match-history/:id', {
			id : '@id'
		});
	};

	this.matchHistoryAll = function() {
		return $resource('/azhu.lol/neo4j/rest/match-history/:id/all', {
			id : '@id'
		});
	};

	this.rankedStats = function() {
		return $resource('/azhu.lol/neo4j/rest/ranked-stats/champions/:id', {
			id : '@id'
		});
	};
});

// Some of these filters are really ugly
neo4jLeagueApp.filter("queueFilter", function() {
	return function(type) {
		var filtered = type ? type.replace("RANKED_SOLO_5x5", "Ranked Solo 5v5") : "";
		filtered = filtered.replace("RANKED_TEAM_5x5", "Ranked Team 5v5");
		filtered = filtered.replace("RANKED_TEAM_3x3", "Ranked Team 3v3");
		return filtered;
	};
}).filter("subTypeFilter", function() {
	return function(type) {
		var filtered = type ? type.replace("RANKED_SOLO_5x5", "Ranked Solo 5v5") : "";
		filtered = filtered.replace("RANKED_TEAM_5x5", "Ranked Team 5v5");
		filtered = filtered.replace("RANKED_TEAM_3x3", "Ranked Team 3v3");
		filtered = filtered.replace("NORMAL_3x3", "Normal 3v3");
		filtered = filtered.replace("BOT_3x3", "Bot 3v3");
		filtered = filtered.replace("ARAM_UNRANKED_5x5", "ARAM");
		filtered = filtered.replace("CAP_5x5", "Team Builder");
		filtered = filtered.replace("ODIN_UNRANKED", "Dominion");
		filtered = filtered.replace("COUNTER_PICK", "Nemesis Draft");
		filtered = filtered.replace("BOT", "Bot");
		filtered = filtered.replace("NORMAL", "Normal 5v5");
		filtered = filtered.replace("NONE", "Custom");
		return filtered;
	};
}).filter('numberFixedLen', function() {
	return function(n, len) {
		var num = parseInt(n, 10);
		len = parseInt(len, 10);
		if (isNaN(num) || isNaN(len)) {
			return n;
		}
		num = '' + num;
		while (num.length < len) {
			num = '0' + num;
		}
		return num;
	};
}).filter('multikill', function() {
	return function(kill) {
		switch (kill) {
		case 2:
			return 'Double Kill';
		case 3:
			return 'Triple Kill';
		case 4:
			return 'Quadrakill';
		case 5:
			return 'Pentakill';
		default:
			return '';
		}
	};
});

neo4jLeagueApp.directive('errSrc', function() {
	return {
		link : function(scope, element, attrs) {
			element.bind('error', function() {
				if (attrs.src != attrs.errSrc) {
					attrs.$set('src', attrs.errSrc);
				}
			});

			attrs.$observe('ngSrc', function(value) {
				if (!value && attrs.errSrc) {
					attrs.$set('src', attrs.errSrc);
				}
			});
		}
	};
});