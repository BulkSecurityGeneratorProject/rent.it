(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('MessageController', MessageController);

    MessageController.$inject = ['$scope', '$state', 'Message', 'MessageSearch'];

    function MessageController ($scope, $state, Message, MessageSearch) {
        var vm = this;
        
        vm.messages = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Message.query(function(result) {
                vm.messages = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            MessageSearch.query({query: vm.searchQuery}, function(result) {
                vm.messages = result;
            });
        }    }
})();
