var leagueApp = angular.module('leagueApp', [ 'ngResource', 'ngRoute' ]);

leagueApp.config([ '$routeProvider', function($routeProvider, routeController) {
	$routeProvider.when('/ranked', {
		templateUrl : 'templates/ranked.html'
	});

	$routeProvider.when('/matches', {
		templateUrl : 'templates/matches.html'
	});

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
} ]);

leagueApp.controller('routeController',
	function($rootScope, $routeParams, LeagueResource) {
		$rootScope.version = "5.2.2";

		LeagueResource.summonerFromId().get({
			id : $routeParams.summonerId
		}, function(data) {
			$rootScope.summoner = data;
		}, function(error) {
			$rootScope.summoner = {};
		});

		$rootScope.showLookupResults = true;
		$rootScope.showRankedResults = false;
		$rootScope.showMatchHistory = false;
	});

leagueApp.controller('lookupController', function($scope, $rootScope, $routeParams,
	LeagueResource) {
	$rootScope.version = "5.2.2";

	if ($routeParams.summonerId) {
		if (!$rootScope.summoner) {
			LeagueResource.summonerFromId().get({
				id : $routeParams.summonerId
			}, function(data) {
				$rootScope.summoner = data;
			}, function(error) {
				$rootScope.summoner = {};
			});
		}
		$rootScope.showLookupResults = true;
	}

	$rootScope.lookup = function() {
		LeagueResource.lookupSummoner().get({
			name : $scope.summonerName
		}, function(data) {
			$rootScope.summoner = data;
		}, function(error) {
			$rootScope.summoner = {};
		});
		$rootScope.showLookupResults = true;
		$rootScope.showRankedResults = false;
		$rootScope.showMatchHistory = false;
	};

	$rootScope.lookupRanked = function(summonerId) {
		$rootScope.newRanked = [];

		LeagueResource.lookupRanked(summonerId).query({
			id : summonerId
		}, function(data) {
			$rootScope.newRanked = data;
		});
		$rootScope.showRankedResults = true;
		$rootScope.showMatchHistory = false;
		$rootScope.showAll = false;
	};

	$rootScope.lookupAllRanked = function(summonerId) {
		LeagueResource.allRanked(summonerId).query({
			id : summonerId
		}, function(data) {
			$rootScope.newRanked = data;
		});
		$rootScope.showAll = true;
	};

	$rootScope.lookupMatches = function(summonerId) {
		$rootScope.newGames = [];
		LeagueResource.matchHistory(summonerId).query({
			id : summonerId
		}, function(data) {
			for (var i = 0; i < data.length; i++) {
				if (data[i].teamId == 100)
					data[i].champion = data[i].blueTeam[data[i].blueTeam.length - 1];
				else
					data[i].champion = data[i].redTeam[data[i].redTeam.length - 1];
			}
			$rootScope.newGames = data;
		});
		$rootScope.showRankedResults = false;
		$rootScope.showMatchHistory = true;
		$rootScope.showAll = false;
	};

	$rootScope.lookupAllMatches = function(summonerId) {
		LeagueResource.matchHistoryAll(summonerId).query({
			id : summonerId
		}, function(data) {
			for (var i = 0; i < data.length; i++) {
				if (data[i].teamId == 100)
					data[i].champion = data[i].blueTeam[data[i].blueTeam.length - 1];
				else
					data[i].champion = data[i].redTeam[data[i].redTeam.length - 1];
			}
			$rootScope.newGames = data;
		});
		$rootScope.showRankedResults = false;
		$rootScope.showMatchHistory = true;
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

		$rootScope.matchPlayerBlue = [];
		$rootScope.matchPlayerRed = [];
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
});

leagueApp.service('LeagueResource', function($resource) {
	this.lookupSummoner = function() {
		return $resource('/azhu.lol/rest/new/summoner/:name');
	};

	this.lookupSummoners = function() {
		return $resource('/azhu.lol/rest/new/summoners/:ids');
	};

	this.summonerFromId = function() {
		return $resource('/azhu.lol/rest/new/summoner/id/:id');
	};

	this.champFromId = function() {
		return $resource('/azhu.lol/rest/champion/:id');
	};

	this.summSpellFromId = function() {
		return $resource('/azhu.lol/rest/summoner-spell/:id');
	};

	this.allRanked = function() {
		return $resource('/azhu.lol/rest/new/ranked-matches/:id/all', {
			id : '@id'
		});
	};

	this.lookupRanked = function() {
		return $resource('/azhu.lol/rest/new/ranked-matches/:id', {
			id : '@id'
		});
	};

	this.matchHistory = function() {
		return $resource('/azhu.lol/rest/new/match-history/:id', {
			id : '@id'
		});
	};
	
	this.matchHistoryAll = function() {
		return $resource('/azhu.lol/rest/new/match-history/:id/all', {
			id : '@id'
		});
	};
});

// Some of these filters are really ugly
leagueApp.filter("queueFilter", function() {
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

leagueApp.directive('errSrc', function() {
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