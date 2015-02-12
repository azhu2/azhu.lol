var leagueApp = angular.module('leagueApp', [ 'ngResource', 'ngRoute' ]);

leagueApp.config(function($routeProvider) {
	$routeProvider.when('/ranked', {
		templateUrl : 'templates/ranked.html'
	});

	$routeProvider.when('/matches', {
		templateUrl : 'templates/matches.html'
	});
});

leagueApp.controller('lookupController', function($scope, LeagueResource) {
	$scope.version = "5.2.2";
	$scope.showLookupResults = false;
	$scope.showRankedResults = false;
	$scope.showMatchHIstory = false;

	$scope.lookup = function() {
		LeagueResource.lookupSummoner().get({
			name : $scope.summonerName
		}, function(data) {
			$scope.summoner = data;
		});
		$scope.showLookupResults = true;
		$scope.showRankedResults = false;
		$scope.showMatchHistory = false;
	};

	$scope.lookupRanked = function(summonerId) {
		$scope.rankedData = [];
		LeagueResource.lookupRanked(summonerId).query({
			id : summonerId
		}, function(data) {
			for (var i = 0; i < data.length; i++) {
				parseRankedGame($scope, data[i], data.length - i - 1);
			}
		});
		$scope.showRankedResults = true;
		$scope.showMatchHistory = false;
	};

	var parseRankedGame = function($scope, match, index) {
		LeagueResource.champFromId().get({
			id : match.participants[0].championId
		}, function(champData) {
			match.champion = champData;
			$scope.rankedData[index] = match;
		});
	};

	$scope.lookupMatches = function(summonerId) {
		$scope.matchData = [];
		LeagueResource.lookupMatches(summonerId).query({
			id : summonerId
		}, function(data) {
			for (var i = 0; i < data.length; i++) {
				parseGame($scope, data[i], data.length - i - 1);
			}
		});
		$scope.showRankedResults = false;
		$scope.showMatchHistory = true;
	};

	var parseGame = function($scope, match, index) {
		LeagueResource.champFromId().get({
			id : match.championId
		}, function(champData) {
			match.champion = champData;
		});

		LeagueResource.summSpellFromId().get({
			id : match.spell1
		}, function(champData) {
			match.sumSpell1 = champData;
		});

		LeagueResource.summSpellFromId().get({
			id : match.spell2
		}, function(champData) {
			match.sumSpell2 = champData;
		});

		$scope.matchData[index] = match;
	};

	$scope.expandMatch = function(match, summoner) {
		if (match.showExpand) {
			match.showExpand = false;
			return;
		}

		closeAllMatches($scope);
		match.showExpand = true;

		var ids = [summoner.id];
		for(var i = 0; i < match.fellowPlayers.length; i++)
			ids.push(match.fellowPlayers[i].summonerId);
		
		
		$scope.matchPlayerBlue = [];
		$scope.matchPlayerRed = [];
		for (var i = 0; i < match.fellowPlayers.length; i++) {
			parsePlayer($scope, match.fellowPlayers[i], match.fellowPlayers.length - i
				- 1);
		}

		// Add current player
		var player = {
			champ : match.champion,
			summoner : summoner
		};
		if (match.teamId === 100)
			$scope.matchPlayerBlue.push(player);
		else
			$scope.matchPlayerRed.push(player);

		// lookupMatch($scope, match); Only useful for ranked
	};

	var parsePlayer = function($scope, player, index) {
		var playerData = {};

		LeagueResource.summonerFromId().get({
			id : player.summonerId
		}, function(summonerData) {
			playerData.summoner = summonerData;
		});

		LeagueResource.champFromId().get({
			id : player.championId
		}, function(champData) {
			playerData.champ = champData;
		});

		if (player.teamId === 100)
			$scope.matchPlayerBlue.push(playerData);
		else
			$scope.matchPlayerRed.push(playerData);
	};

	var lookupMatch = function($scope, match) {
		LeagueResource.matchDetail().get({
			id : match.gameId
		}, function(matchData) {
			$scope.matchDetails = matchData;
		});
	};

	var closeAllMatches = function($scope) {
		for (var i = 0; i < $scope.matchData.length; i++) {
			$scope.matchData[i].showExpand = false;
		}
	};
});

leagueApp.service('LeagueResource', function($resource) {
	this.lookupSummoner = function() {
		return $resource('/azhu.lol/rest/summoner/:name');
	};
	
	this.lookupSummoners = function() {
		return $resource('/azhu.lol/rest/summoners/:ids');
	};

	this.summonerFromId = function() {
		return $resource('/azhu.lol/rest/summoner/id/:id');
	};

	this.lookupRanked = function() {
		return $resource('/azhu.lol/rest/ranked-matches/:id');
	};

	this.champFromId = function() {
		return $resource('/azhu.lol/rest/champion/:id');
	};

	this.lookupMatches = function() {
		return $resource('/azhu.lol/rest/match-history/:id');
	};

	this.matchDetail = function() {
		return $resource('/azhu.lol/rest/match/:id');
	};

	this.summSpellFromId = function() {
		return $resource('/azhu.lol/rest/summoner-spell/:id');
	}
});

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
		filtered = filtered.replace("NORMAL", "Normal 5v5");
		filtered = filtered.replace("BOT", "Bot");
		filtered = filtered.replace("NORMAL_3x3", "Normal 3v3");
		filtered = filtered.replace("BOT_3x3", "Bot 3v3");
		filtered = filtered.replace("ARAM_UNRANKED_5x5", "ARAM");
		filtered = filtered.replace("CAP_5x5", "Dominion");
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
});
;

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