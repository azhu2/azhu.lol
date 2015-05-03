var version = "5.6.1";

var neo4jLeagueApp = angular.module('neo4jLeagueApp', [ 'ngResource', 'ngRoute', 'angular.filter' ]);

neo4jLeagueApp.config([ '$routeProvider', function($routeProvider, routeController) {
	$routeProvider.when('/:summId', {
		controller : 'routeController',
		templateUrl : 'blank.html' // idk why, but this is needed
	});

	$routeProvider.when('/:summId/matches', {
		controller : 'matchesController',
		templateUrl : 'templates/matches.html'
	});

	$routeProvider.when('/:summId/ranked', {
		controller : 'rankedMatchesController',
		templateUrl : 'templates/ranked.html'
	});

	$routeProvider.when('/:summId/ranked-stats', {
		controller : 'rankedStatsController',
		templateUrl : 'templates/ranked-stats.html'
	});

	$routeProvider.when('/:summId/general-stats', {
		controller : 'generalStatsController',
		templateUrl : 'templates/general-stats.html'
	});
	
	$routeProvider.when('/:summId/player-stats', {
		controller : 'playerStatsController',
		templateUrl : 'templates/player-stats.html'
	});
} ]);

var lookupSummonerById = function($rootScope, summonerId, LeagueResource) {
	$rootScope.version = version;
	$rootScope.showTabs = false;
	$rootScope.summonerId = summonerId;

	LeagueResource.summonerFromId().get({
		id : summonerId
	}, function(data) {
		$rootScope.summoner = data;
		$rootScope.showTabs = true;
	}, function(error) {
		$rootScope.summoner = {};
	});

	$rootScope.showLookupResults = true;
	$rootScope.showTab = false;
};

var routeController; // IDE navigation marker
neo4jLeagueApp.controller('routeController', function($rootScope, $routeParams, LeagueResource) {
	$rootScope.version = version;
	$rootScope.showTabs = false;

	// Lookup summoner if not done yet
	if ($routeParams && $routeParams.summId
		&& (!$rootScope.summonerId || ($rootScope.summonerId != $routeParams.summId)))
		lookupSummonerById($rootScope, $routeParams.summId, LeagueResource);
	else
		$rootScope.showTabs = true;
});

var lookupController; // IDE navigation marker
neo4jLeagueApp.controller('lookupController', function($scope, $rootScope, $routeParams, LeagueResource) {
	$rootScope.version = version;
	var lookupSummoner;

	if ($routeParams.summId) {
		if (!$rootScope.summoner) {
			LeagueResource.summonerFromId().get({
				id : $routeParams.summId
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
});

var matchesController; // IDE navigation marker
neo4jLeagueApp.controller('matchesController', function($scope, $rootScope, $routeParams, LeagueResource) {
	var getMatches = function(summonerId) {
		clearData($rootScope);
		lookupSummoner = summonerId;

		LeagueResource.matchHistory(summonerId).query({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner) {
				for (var i = 0; i < data.length; i++) {
					// Hella hacky, but it works...
					if (data[i].teamId == 100)
						data[i].champion = data[i].blueTeam[0];
					else
						data[i].champion = data[i].redTeam[0];
				}
				$rootScope.newGames = data;
			}
		});
		$rootScope.showTab = true;
		$rootScope.showAll = false;
	};

	$scope.lookupMatches = function() {
		// Lookup summoner if not done yet
		if ($routeParams && $routeParams.summId
			&& (!$rootScope.summonerId || ($rootScope.summonerId != $routeParams.summId))) {
			$rootScope.version = version;
			$rootScope.showTabs = false;

			lookupSummonerById($rootScope, $routeParams.summId, LeagueResource);
			getMatches($routeParams.summId);
		} else
			getMatches($rootScope.summoner.id);
	};

	$scope.lookupAllMatches = function() {
		var summonerId = $rootScope.summoner.id;
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
		match.showExpand = true;
	};
});

var generalStatsController; // IDE navigation marker
neo4jLeagueApp.controller('generalStatsController', function($scope, $rootScope, $routeParams, LeagueResource) {
	var lookupStats = function(summonerId) {
		clearData($rootScope);
		lookupSummoner = summonerId;

		LeagueResource.generalStats(summonerId).get({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner)
				$rootScope.generalStats = data;
		});
		$rootScope.showTab = true;
	};

	$scope.getGeneralStats = function() {
		// Lookup summoner if not done yet
		if ($routeParams && $routeParams.summId
			&& (!$rootScope.summonerId || ($rootScope.summonerId != $routeParams.summId))) {
			$rootScope.version = version;
			$rootScope.showTabs = false;

			lookupSummonerById($rootScope, $routeParams.summId, LeagueResource);
			lookupStats($routeParams.summId);
		} else
			lookupStats($rootScope.summoner.id);
	};

	$scope.sortBy = function(sortColumn) {
		if (sortColumn === $scope.sort) {
			$scope.reverse = !$scope.reverse;
			return;
		}
		$scope.sort = sortColumn;
		$scope.reverse = true;
	};

	$scope.expandChampion = function(queueData, champData) {
		for (var i = 0; i < queueData.length; i++) {
			if (queueData[i] == champData)
				queueData[i].showDetails = !queueData[i].showDetails;
			else
				queueData[i].showDetails = false;

		}
	};
});

var rankedMatchesController; // IDE navigation marker
neo4jLeagueApp.controller('rankedMatchesController', function($scope, $rootScope, $routeParams, LeagueResource) {
	var setRankedMatches = function(summonerId, data) {
		for (var i = 0; i < data.length; i++) {
			var match = data[i];
			var players = match.players;
			for (var j = 0; j < players.length; j++)
				if (players[j].summoner.id == summonerId) {
					match.lookupPlayer = j;
				}
		}

		$rootScope.newRanked = data;
	};

	var getRanked = function(summonerId) {
		lookupSummoner = summonerId;
		clearData($rootScope);

		LeagueResource.lookupRanked(summonerId).query({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner)
				setRankedMatches(summonerId, data);
		});
		$rootScope.showTab = true;
		$rootScope.showAll = false;

		// Pagination
		LeagueResource.allRanked(summonerId).query({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner)
				$rootScope.totalMatches = data.length;
		});
		$rootScope.startIndex = 0;
	};

	$scope.lookupRanked = function() {
		// Lookup summoner if not done yet
		if ($routeParams && $routeParams.summId
			&& (!$rootScope.summonerId || ($rootScope.summonerId != $routeParams.summId))) {
			$rootScope.version = version;
			$rootScope.showTabs = false;

			lookupSummonerById($rootScope, $routeParams.summId, LeagueResource);
			getRanked($routeParams.summId);
		} else
			getRanked($rootScope.summoner.id);
	};

	$scope.lookupRankedWithOffset = function(summonerId, offset) {
		clearData($rootScope);

		LeagueResource.lookupRanked(summonerId).query({
			id : summonerId,
			start : offset
		}, function(data) {
			if (summonerId == lookupSummoner)
				setRankedMatches(summonerId, data);
		});
		$rootScope.startIndex = offset;
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

	$scope.working = ' (slow)';

	// Used to check before pushing data if it's for the right match
	var expandedMatch = 0;

	$rootScope.expandMatch = function(match, summoner) {
		if (match.showExpand) {
			match.showExpand = false;
			return;
		}

		expandedMatch++;
		match.showExpand = true;
	};
});

var rankedStatsController; // IDE navigation marker
neo4jLeagueApp.controller('rankedStatsController', function($scope, $rootScope, $routeParams, LeagueResource) {
	var lookupRankedStats = function(summonerId) {
		clearData($rootScope);
		lookupSummoner = summonerId;

		LeagueResource.rankedStats(summonerId).query({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner)
				$rootScope.rankedStats = data;
		});
		$rootScope.showTab = true;
	};

	$scope.getRankedStats = function() {
		// Lookup summoner if not done yet
		if ($routeParams && $routeParams.summId
			&& (!$rootScope.summonerId || ($rootScope.summonerId != $routeParams.summId))) {
			$rootScope.version = version;
			$rootScope.showTabs = false;

			lookupSummonerById($rootScope, $routeParams.summId, LeagueResource);
			lookupRankedStats($routeParams.summId);
		} else
			lookupRankedStats($rootScope.summoner.id);
	};

	$scope.sortBy = function(sortColumn) {
		if (sortColumn === $scope.sort) {
			$scope.reverse = !$scope.reverse;
			return;
		}
		$scope.sort = sortColumn;
		$scope.reverse = true;
	};
});

var playerStatsController; // IDE navigation marker
neo4jLeagueApp.controller('playerStatsController', function($scope, $rootScope, $routeParams, LeagueResource) {
	var lookupPlayerStats = function(summonerId) {
		clearData($rootScope);
		lookupSummoner = summonerId;

		LeagueResource.playerStats(summonerId).get({
			id : summonerId
		}, function(data) {
			if (summonerId == lookupSummoner)
				$rootScope.playerStats = data;
		});
		$rootScope.showTab = true;
	};

	$scope.getPlayerStats = function() {
		// Lookup summoner if not done yet
		if ($routeParams && $routeParams.summId
			&& (!$rootScope.summonerId || ($rootScope.summonerId != $routeParams.summId))) {
			$rootScope.version = version;
			$rootScope.showTabs = false;

			lookupSummonerById($rootScope, $routeParams.summId, LeagueResource);
			lookupPlayerStats($routeParams.summId);
		} else
			lookupPlayerStats($rootScope.summoner.id);
	};
});

var clearData = function($rootScope) {
	$rootScope.newGames = [];
	$rootScope.newRanked = [];
	$rootScope.rankedStats = [];
	$rootScope.generalStats = [];
	$rootScope.playerStats = [];
};

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

	this.generalStats = function() {
		return $resource('/azhu.lol/neo4j/rest/general-stats/:id', {
			id : '@id'
		});
	};
	
	this.playerStats = function() {
		return $resource('/azhu.lol/neo4j/rest/player-stats/:id', {
			id : '@id'
		});
	};
});

neo4jLeagueApp.filter("queueFilter", function() {
	return function(type) {
		switch (type) {
		case "RANKED_SOLO_5x5":
			return "Ranked Solo 5v5";
		case "RANKED_TEAM_5x5":
			return "Ranked Team 5v5";
		case "RANKED_TEAM_3x3":
			return "Ranked Team 3v3";
		default:
			return type;
		}
	};
}).filter("subTypeFilter", function() {
	return function(type) {
		switch (type) {
		case "RANKED_SOLO_5x5":
			return "Ranked Solo 5v5";
		case "RANKED_TEAM_5x5":
			return "Ranked Team 5v5";
		case "RANKED_TEAM_3x3":
			return "Ranked Team 3v3";
		case "NORMAL_3x3":
			return "Normal 3v3";
		case "BOT_3x3":
			return "Bot 3v3";
		case "ARAM_UNRANKED_5x5":
			return "ARAM";
		case "CAP_5x5":
			return "Team Builder";
		case "ODIN_UNRANKED":
			return "Dominion";
		case "COUNTER_PICK":
			return "Nemesis Draft";
		case "BOT":
			return "Bot";
		case "NORMAL":
			return "Normal 5v5";
		case "URF_BOT":
			return "URF Bot.";
		case "URF":
			return "Ultra Rapid Fire!";
		case "NONE":
			return "Custom";
		default:
			return type;
		}
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