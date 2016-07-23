(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('BookingController', BookingController);

    BookingController.$inject = ['$scope', '$state', 'Booking', 'BookingSearch'];

    function BookingController ($scope, $state, Booking, BookingSearch) {
        var vm = this;
        
        vm.bookings = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Booking.query(function(result) {
                vm.bookings = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            BookingSearch.query({query: vm.searchQuery}, function(result) {
                vm.bookings = result;
            });
        }    }
})();
