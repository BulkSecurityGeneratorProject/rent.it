'use strict';

describe('Controller Tests', function() {

    describe('Booking Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockBooking, MockProduct, MockTimeSlot;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockBooking = jasmine.createSpy('MockBooking');
            MockProduct = jasmine.createSpy('MockProduct');
            MockTimeSlot = jasmine.createSpy('MockTimeSlot');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Booking': MockBooking,
                'Product': MockProduct,
                'TimeSlot': MockTimeSlot
            };
            createController = function() {
                $injector.get('$controller')("BookingDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'rentitApp:bookingUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
