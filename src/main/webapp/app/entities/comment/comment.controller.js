(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('CommentController', CommentController);

    CommentController.$inject = ['$scope', '$state', 'Comment', 'CommentSearch'];

    function CommentController ($scope, $state, Comment, CommentSearch) {
        var vm = this;
        
        vm.comments = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Comment.query(function(result) {
                vm.comments = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            CommentSearch.query({query: vm.searchQuery}, function(result) {
                vm.comments = result;
            });
        }    }
})();
