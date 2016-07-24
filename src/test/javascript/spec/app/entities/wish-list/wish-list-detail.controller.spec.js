'use strict';

describe('Controller Tests', function() {

    describe('WishList Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockWishList, MockUser, MockProduct;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockWishList = jasmine.createSpy('MockWishList');
            MockUser = jasmine.createSpy('MockUser');
            MockProduct = jasmine.createSpy('MockProduct');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'WishList': MockWishList,
                'User': MockUser,
                'Product': MockProduct
            };
            createController = function() {
                $injector.get('$controller')("WishListDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'rentitApp:wishListUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
