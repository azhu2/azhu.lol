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

leagueApp.controller('lookupController',
	function($scope, $rootScope, $routeParams, LeagueResource) {
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
			$rootScope.rankedData = [];
			LeagueResource.lookupRanked(summonerId).query({
				id : summonerId
			}, function(data) {
				for (var i = 0; i < data.length; i++) {
					parseRankedGame($rootScope, data[i], data.length - i - 1);
				}
			});

			LeagueResource.newRanked(summonerId).query({
				id : summonerId
			}, function(data) {
				$rootScope.newRanked = data;
			});
			$rootScope.showRankedResults = true;
			$rootScope.showMatchHistory = false;
			$rootScope.showAll = false;
		};

		$rootScope.lookupAllRanked = function(summonerId) {
			$rootScope.rankedData = [];
			LeagueResource.allRanked(summonerId).query({
				id : summonerId
			}, function(data) {
				for (var i = 0; i < data.length; i++) {
					parseRankedGame($rootScope, data[i], data.length - i - 1);
				}
			});
			$rootScope.showAll = true;
		};

		var parseRankedGame = function($rootScope, match, index) {
			LeagueResource.champFromId().get({
				id : match.participants[0].championId
			}, function(champData) {
				match.champion = champData;
				$rootScope.rankedData[index] = match;
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

		$rootScope.lookupMatches = function(summonerId) {
			$rootScope.matchData = [];
			LeagueResource.lookupMatches(summonerId).query({
				id : summonerId
			}, function(data) {
				for (var i = 0; i < data.length; i++) {
					parseGame($rootScope, data[i], data.length - i - 1);
				}
			});
			$rootScope.showRankedResults = false;
			$rootScope.showMatchHistory = true;
			$rootScope.showAll = false;
		};

		$rootScope.lookupAllMatches = function(summonerId) {
			$rootScope.matchData = [];
			LeagueResource.lookupAllMatches(summonerId).query({
				id : summonerId
			}, function(data) {
				for (var i = 0; i < data.length; i++) {
					parseGame($rootScope, data[i], data.length - i - 1);
				}
			});
			$rootScope.showAll = true;
		};

		// Process champion and summoner spells
		var parseGame = function($rootScope, match, index) {
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

			$rootScope.matchData[index] = match;
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
				lookupSummonerIds($rootScope, ids, champs, teamIds, expandedMatch);
			}

			if (isRanked(match)) {
				lookupMatch($rootScope, match, expandedMatch);
			}
		};

		var lookupMatch = function($rootScope, match, expandedMatch) {
			LeagueResource.matchDetail().get(
				{
					id : match.matchId
				},
				function(matchData) {
					$rootScope.matchDetails = matchData;

					lookupRankedSummonerIds($rootScope, matchData.participantIdentities,
						matchData, expandedMatch);
				});
		};

		var lookupRankedSummonerIds = function($rootScope, identities, details,
			expandedMatch) {
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
						parseRankedPlayer($rootScope, matchPlayers,
							details.participantIdentities[i], details.participants[i],
							summoners[i], expandedMatch, i);
					}
				});
		};

		// Lookup for expanding a match
		var lookupSummonerIds = function($rootScope, players, champs, teamIds, matchId) {
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

						parsePlayer($rootScope, matchPlayers, champId, summoner, teamIds,
							matchId, i);
					}
				});
		};

		var parseRankedPlayer = function($rootScope, matchPlayers, ids, details,
			summoner, matchId, index) {
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
					$rootScope.matchPlayerBlue.push(matchPlayers[index]);
				else
					$rootScope.matchPlayerRed.push(matchPlayers[index]);
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
		var parsePlayer = function($rootScope, matchPlayers, champId, summoner, teamIds,
			matchId, index) {
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
					$rootScope.matchPlayerBlue.push(matchPlayers[index]);
				else
					$rootScope.matchPlayerRed.push(matchPlayers[index]);
			});
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

		// Very dirty, but it works fine
		var isRanked = function(match) {
			return match.season != null;
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

	this.lookupAllMatches = function() {
		return $resource('/azhu.lol/rest/match-history/:id/all');
	};

	this.matchDetail = function() {
		return $resource('/azhu.lol/rest/match/:id');
	};

	this.summSpellFromId = function() {
		return $resource('/azhu.lol/rest/summoner-spell/:id');
	};

	this.allRanked = function() {
		return $resource('/azhu.lol/rest/ranked-matches/:id/all', {
			id : '@id'
		});
	};

	this.newRanked = function() {
		return $resource('/azhu.lol/rest/new/ranked-matches/:id', {
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