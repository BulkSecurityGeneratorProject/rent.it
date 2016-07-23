(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('TimeSlotController', TimeSlotController);

    TimeSlotController.$inject = ['$scope', '$state', 'TimeSlot', 'TimeSlotSearch'];

    function TimeSlotController ($scope, $state, TimeSlot, TimeSlotSearch) {
        var vm = this;
        
        vm.timeSlots = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            TimeSlot.query(function(result) {
                vm.timeSlots = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            TimeSlotSearch.query({query: vm.searchQuery}, function(result) {
                vm.timeSlots = result;
            });
        }    }
})();
