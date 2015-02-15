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
		}, function(error) {
			$scope.summoner = {};
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

		LeagueResource.summSpellFromId().get({
			id : match.participants[0].spell1Id
		}, function(champData) {
			match.sumSpell1 = champData;
		});

		LeagueResource.summSpellFromId().get({
			id : match.participants[0].spell2Id
		}, function(champData) {
			match.sumSpell2 = champData;
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

	// Process champion and summoner spells
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

	// Used to check before pushing data if it's for the right match
	var expandedMatch = 0;

	$scope.expandMatch = function(match, summoner) {
		if (match.showExpand) {
			match.showExpand = false;
			return;
		}

		expandedMatch++;
		closeAllMatches($scope);
		match.showExpand = true;

		// This part is terrible
		if (!isRanked(match)) {
			var ids = [ summoner.id ];
			var champs = [ match.championId ];
			var teamIds = [ match.teamId ];
			for (var i = 0; i < match.fellowPlayers.length; i++) {
				ids.push(match.fellowPlayers[i].summonerId);
				champs.push(match.fellowPlayers[i].championId);
				teamIds.push(match.fellowPlayers[i].teamId);
			}
			lookupSummonerIds($scope, ids, champs, teamIds, expandedMatch);
		}

		if (isRanked(match)) {
			lookupMatch($scope, match, expandedMatch);
		}
	};

	var lookupMatch = function($scope, match, expandedMatch) {
		LeagueResource.matchDetail().get(
			{
				id : match.matchId
			},
			function(matchData) {
				$scope.matchDetails = matchData;

				lookupRankedSummonerIds($scope, matchData.participantIdentities,
					matchData, expandedMatch);
			});
	};

	var lookupRankedSummonerIds = function($scope, identities, details, expandedMatch) {
		var ids = "";
		for (var i = 0; i < identities.length; i++)
			ids += identities[i].player.summonerId + ",";

		LeagueResource.lookupSummoners().query(
			{
				ids : ids
			},
			function(summoners) {
				var matchPlayers = [];

				for (var i = 0; i < details.participants.length; i++) {
					parseRankedPlayer($scope, matchPlayers,
						details.participantIdentities[i], details.participants[i],
						summoners[i], expandedMatch, i);
				}
			});
	};

	// Lookup for expanding a match
	var lookupSummonerIds = function($scope, players, champs, teamIds, matchId) {
		var ids = "";
		for (var i = 0; i < players.length; i++)
			ids += players[i] + ",";

		LeagueResource.lookupSummoners().query(
			{
				ids : ids
			},
			function(summoners) {
				var matchPlayers = [];

				for (var i = 0; i < summoners.length; i++) {
					var champId = champs[i];
					var summoner = summoners[i];

					parsePlayer($scope, matchPlayers, champId, summoner, teamIds,
						matchId, i);
				}
			});
	};

	var parseRankedPlayer = function($scope, matchPlayers, ids, details, summoner,
		matchId, index) {
		if (matchPlayers[index] == null)
			matchPlayers[index] = {};
		matchPlayers[index].summoner = summoner;
		matchPlayers[index].details = details;

		LeagueResource.champFromId().get({
			id : details.championId
		}, function(champData) {
			matchPlayers[index].champ = champData;

			if (expandedMatch != matchId)
				return;
			if (details.teamId === 100)
				$scope.matchPlayerBlue.push(matchPlayers[index]);
			else
				$scope.matchPlayerRed.push(matchPlayers[index]);
		});
		
		LeagueResource.summSpellFromId().get({
			id : details.spell1Id
		}, function(spellData) {
			matchPlayers[index].spell1 = spellData;
		});
		
		LeagueResource.summSpellFromId().get({
			id : details.spell2Id
		}, function(spellData) {
			matchPlayers[index].spell2 = spellData;
		});
	};

	// Process a single player in a match
	var parsePlayer = function($scope, matchPlayers, champId, summoner, teamIds, matchId,
		index) {
		if (matchPlayers[index] == null)
			matchPlayers[index] = {};
		matchPlayers[index].summoner = summoner;

		LeagueResource.champFromId().get({
			id : champId
		}, function(champData) {
			matchPlayers[index].champ = champData;

			if (expandedMatch != matchId)
				return;
			if (teamIds[index] === 100)
				$scope.matchPlayerBlue.push(matchPlayers[index]);
			else
				$scope.matchPlayerRed.push(matchPlayers[index]);

		});
	};

	var closeAllMatches = function($scope) {
		if ($scope.matchData != null)
			for (var i = 0; i < $scope.matchData.length; i++) {
				$scope.matchData[i].showExpand = false;
			}

		if ($scope.rankedData != null)
			for (var i = 0; i < $scope.rankedData.length; i++) {
				$scope.rankedData[i].showExpand = false;
			}

		$scope.matchPlayerBlue = [];
		$scope.matchPlayerRed = [];
	};

	// Very dirty, but it works fine
	var isRanked = function(match) {
		return match.season != null;
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
		filtered = filtered.replace("NORMAL", "Normal 5v5");
		filtered = filtered.replace("BOT", "Bot");
		filtered = filtered.replace("NORMAL_3x3", "Normal 3v3");
		filtered = filtered.replace("BOT_3x3", "Bot 3v3");
		filtered = filtered.replace("ARAM_UNRANKED_5x5", "ARAM");
		filtered = filtered.replace("CAP_5x5", "Team Builder");
		filtered = filtered.replace("ODIN_UNRANKED", "Dominion");
		filtered = filtered.replace("COUNTER_PICK", "Nemesis Draft");
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